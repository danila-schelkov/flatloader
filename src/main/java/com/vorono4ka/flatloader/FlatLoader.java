package com.vorono4ka.flatloader;

import com.vorono4ka.flatloader.annotations.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlatLoader {
    private final FlatByteStream stream;

    public FlatLoader(Path filepath) throws IOException {
        byte[] bytes = Files.readAllBytes(filepath);
        stream = new FlatByteStream(bytes);
    }

    public FlatLoader(FlatByteStream stream) {
        this.stream = stream;
    }

    public <T> T deserializeClass(Class<T> serializable) {
        T instance;

        int structureSize = 0;
        VTable vTable = null;
        VTableClass vTableClass = serializable.getAnnotation(VTableClass.class);
        if (vTableClass != null) {
            vTable = deserializeRootTable();
        } else {
            StructureSize structureSizeAnnotation = serializable.getAnnotation(StructureSize.class);
            if (structureSizeAnnotation != null) {
                structureSize = structureSizeAnnotation.value();
            }
        }

        try {
            int structureStartPosition = stream.tell();

            instance = serializable.getDeclaredConstructor().newInstance();
            for (Field declaredField : instance.getClass().getDeclaredFields()) {
                if (handleOffset(declaredField, vTable, structureStartPosition, structureSize))
                    continue;

                handleField(instance, declaredField);
            }

            if (vTable != null || structureSize != 0) {
                stream.seek(structureStartPosition + structureSize);
            }
        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    public <T> T deserializeRecord(Class<T> serializable) {
        Constructor<T> suitableRecordConstructor = getSuitableRecordConstructor(serializable);
        if (suitableRecordConstructor == null) {
            throw new IllegalStateException("Cannot find suitable constructor for record: " + serializable.getSimpleName());
        }

        int structureSize = 0;
        VTable vTable = null;
        VTableClass vTableClass = serializable.getAnnotation(VTableClass.class);
        if (vTableClass != null) {
            vTable = deserializeRootTable();
        } else {
            StructureSize structureSizeAnnotation = serializable.getAnnotation(StructureSize.class);
            if (structureSizeAnnotation != null) {
                structureSize = structureSizeAnnotation.value();
            }
        }

        int parameterCount = suitableRecordConstructor.getParameterCount();

        int structureStartPosition = stream.tell();

        int i = 0;
        Object[] parameters = new Object[parameterCount];
        for (Parameter parameter : suitableRecordConstructor.getParameters()) {
            if (handleOffset(parameter, vTable, structureStartPosition, structureSize)) {
                parameters[i++] = getDefaultParameterValue(parameter);
                continue;
            }

            parameters[i++] = deserializeParameter(parameter);
        }

        if (vTable != null || structureSize != 0) {
            stream.seek(structureStartPosition + structureSize);
        }

        try {
            return suitableRecordConstructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Constructor<T> getSuitableRecordConstructor(Class<T> serializableRecord) {
        Constructor<?>[] declaredConstructors = serializableRecord.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            //noinspection unchecked
            return (Constructor<T>) declaredConstructor;
        }

        return null;
    }

    private static Class<?> getSerializationClass(Class<?> type, FlatType flatTypeAnnotation) {
        if (flatTypeAnnotation == null) return type;

        SerializeType flatType = flatTypeAnnotation.value();
        return flatType.getSerializationClass();
    }

    private boolean handleOffset(AnnotatedElement element, VTable vTable, int structureStartPosition, int structureSize) {
        if (vTable != null) {
            VTableField vTableField = element.getDeclaredAnnotation(VTableField.class);
            if (vTableField == null) {
                throw new IllegalArgumentException("Given element is not annotated with " + VTableField.class.getSimpleName() + ": " + element);
            }

            short fieldOffset = vTable.getFieldOffset(vTableField.value());
            if (fieldOffset <= 0) {  // Value is not present in this table
                return true;
            }

            stream.seek(vTable.position() + fieldOffset);
        } else if (structureSize != 0) {
            Offset offsetAnnotation = element.getDeclaredAnnotation(Offset.class);
            if (offsetAnnotation == null) {
                return true;
            }

            stream.seek(structureStartPosition + offsetAnnotation.value());
        }

        return false;
    }

    private <T> void handleField(T instance, Field declaredField) throws IllegalAccessException {
        int modifiers = declaredField.getModifiers();
        if (Modifier.isStatic(modifiers)) return;
        if (Modifier.isTransient(modifiers)) return;

        boolean accessChanged = !declaredField.canAccess(instance);
        if (accessChanged) {
            declaredField.setAccessible(true);
        }

        Object value = deserializeField(declaredField);
        declaredField.set(instance, value);

        if (accessChanged) {
            declaredField.setAccessible(false);
        }
    }

    private <T> T deserializeField(Field field) {
        System.out.println(field);

        Class<?> type = field.getType();

        FlatChunk flatChunkAnnotation = field.getDeclaredAnnotation(FlatChunk.class);
        if (flatChunkAnnotation != null) {
            SerializeType countSerializeType = flatChunkAnnotation.countType();
            int count = deserializePrimitive(countSerializeType.getSerializationClass());
            FlatLoader flatLoader = new FlatLoader(new FlatByteStream(stream.read(count)));
            //noinspection unchecked
            return (T) flatLoader.deserializeClass(type);
        }

        FlatReference flatReference = field.getDeclaredAnnotation(FlatReference.class);
        if (flatReference != null) {
            int position = stream.tell();
            int offset = deserializePrimitive(flatReference.value().getSerializationClass());
            int nextPosition = stream.tell();
            stream.seek(position + offset);
            T result = deserializeField0(field, type);
            stream.seek(nextPosition);
            return result;
        }

        return deserializeField0(field, type);
    }

    private <T> T deserializeField0(Field field, Class<?> type) {
        if (type.isAssignableFrom(ArrayList.class)) {
            //noinspection unchecked
            return (T) deserializeCollection(field.getGenericType(), field.getType());
        }

        return deserializeByType(type, field.getDeclaredAnnotation(FlatType.class));
    }

    private <T> T deserializeParameter(Parameter parameter) {
        Class<?> type = parameter.getType();

        FlatReference flatReference = parameter.getDeclaredAnnotation(FlatReference.class);
        if (flatReference != null) {
            int position = stream.tell();
            int offset = deserializePrimitive(flatReference.value().getSerializationClass());
            int nextPosition = stream.tell();
            stream.seek(position + offset);
            T result = deserializeParameter0(parameter, type);
            stream.seek(nextPosition);
            return result;
        }

        return deserializeParameter0(parameter, type);
    }

    private <T> T deserializeParameter0(Parameter parameter, Class<?> type) {
        if (type.isAssignableFrom(ArrayList.class)) {
            //noinspection unchecked
            return (T) deserializeCollection(type, type);
        }

        return deserializeByType(type, parameter.getDeclaredAnnotation(FlatType.class));
    }

    private <T> T deserializeByType(Class<?> type, FlatType flatTypeAnnotation) {
        Class<?> serializationClass = getSerializationClass(type, flatTypeAnnotation);
        if (isPrimitive(serializationClass)) {
            return deserializePrimitive(serializationClass);
        }

        if (type == VTable.class) {
            //noinspection unchecked
            return (T) deserializeVTable();
        }

        if (type.isRecord()) {
            //noinspection unchecked
            return (T) deserializeRecord(type);
        }

        //noinspection unchecked
        return (T) deserializeClass(type);
    }

    private List<?> deserializeCollection(Type type, Class<?> aClass) {
        ParameterizedType listType = (ParameterizedType) type;
        Class<?> listElementClass = (Class<?>) listType.getActualTypeArguments()[0];

        List<Object> list;
        try {
            //noinspection unchecked
            list = (List<Object>) aClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        int count = stream.readInt32();

        for (int i = 0; i < count; i++) {
            Object object;
            if (listElementClass.isRecord()) {
                object = deserializeRecord(listElementClass);
            } else if (isPrimitive(listElementClass)) {
                object = deserializePrimitive(listElementClass);
            } else {
                object = deserializeClass(listElementClass);
            }

            list.add(object);
        }

        return list;
    }

    private VTable deserializeRootTable() {
        int position = stream.tell();
        int offset = stream.readInt32();
        int nextPosition = stream.tell();
        stream.seek(position + offset);
        VTable vTable = deserializeVTable();
        stream.seek(nextPosition);
        return vTable;
    }

    private VTable deserializeVTable() {
        int position = stream.tell();
        int offset = stream.readInt32();
        stream.seek(position - offset);
        short vtableSize = stream.readInt16();  // starting from here
        short dataSize = stream.readInt16();
        int fieldCount = (vtableSize - 4) / 2;
        short[] fieldOffsets = stream.readInt16Array(fieldCount);
        return new VTable(position, dataSize, fieldOffsets);
    }

    private <T> T deserializePrimitive(Class<?> primiviteClass) {
        if (isLong(primiviteClass)) {
            //noinspection unchecked
            return (T) (Long) stream.readInt64();
        } else if (isInt(primiviteClass)) {
            //noinspection unchecked
            return (T) (Integer) stream.readInt32();
        } else if (isShort(primiviteClass)) {
            //noinspection unchecked
            return (T) (Short) stream.readInt16();
        } else if (isByte(primiviteClass)) {
            //noinspection unchecked
            return (T) (Byte) stream.readInt8();
        } else if (isBoolean(primiviteClass)) {
            //noinspection unchecked
            return (T) (Boolean) stream.readBoolean();
        }  else if (isFloat(primiviteClass)) {
            //noinspection unchecked
            return (T) (Float) stream.readFloat();
        } else if (primiviteClass == String.class) {
            // Note: it seems string is always encoded as an offset to c-string with \0
            int position = stream.tell();
            int offset = deserializePrimitive(Integer.class);
            int nextPosition = stream.tell();
            stream.seek(position + offset);
            String result = stream.readString();
            stream.seek(nextPosition);

            //noinspection unchecked
            return (T) result;
        }

        throw new IllegalArgumentException("Provided type is not primitive! Provided class: " + primiviteClass.getSimpleName());
    }

    private static boolean isPrimitive(Class<?> serializationClass) {
        return serializationClass.isPrimitive() || isPrimitiveNumber(serializationClass) || isFloat(serializationClass) || isBoolean(serializationClass) || serializationClass == String.class;
    }

    private static boolean isPrimitiveNumber(Class<?> serializationClass) {
        return serializationClass == Long.class || serializationClass == Integer.class || serializationClass == Short.class || serializationClass == Byte.class ||
            serializationClass == long.class || serializationClass == int.class || serializationClass == short.class || serializationClass == byte.class;
    }

    private static boolean isBoolean(Class<?> primiviteClass) {
        return primiviteClass == Boolean.class || primiviteClass == boolean.class;
    }

    private static boolean isByte(Class<?> primiviteClass) {
        return primiviteClass == Byte.class || primiviteClass == byte.class;
    }

    private static boolean isShort(Class<?> primiviteClass) {
        return primiviteClass == Short.class || primiviteClass == short.class;
    }

    private static boolean isInt(Class<?> primiviteClass) {
        return primiviteClass == Integer.class || primiviteClass == int.class;
    }

    private static boolean isFloat(Class<?> primiviteClass) {
        return primiviteClass == Float.class || primiviteClass == float.class;
    }

    private static boolean isLong(Class<?> primiviteClass) {
        return primiviteClass == Long.class || primiviteClass == long.class;
    }

    private Object getDefaultParameterValue(Parameter parameter) {
        DefaultValue defaultValueAnnotation = parameter.getDeclaredAnnotation(DefaultValue.class);
        if (defaultValueAnnotation == null) {
            throw new IllegalStateException("Cannot set default value: Annotation " + DefaultValue.class.getSimpleName() + " is not set to parameter \"" + parameter + "\"");
        }

        Class<?> type = parameter.getType();
        if (isLong(type)) {
            return defaultValueAnnotation.longValue();
        } else if (isInt(type)) {
            return defaultValueAnnotation.intValue();
        } else if (isShort(type)) {
            return defaultValueAnnotation.shortValue();
        } else if (isByte(type)) {
            return defaultValueAnnotation.byteValue();
        } else if (isBoolean(type)) {
            return defaultValueAnnotation.booleanValue();
        } else if (isFloat(type)) {
            return defaultValueAnnotation.floatValue();
        } else if (type == String.class) {
            return defaultValueAnnotation.stringValue();
        }

        return null;
    }
}
