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

    public <T> T deserializeClass(Class<T> serializableClass) {
        T instance;

        VTable vTable = null;
        VTableClass vTableClass = serializableClass.getAnnotation(VTableClass.class);
        if (vTableClass != null) {
            vTable = deserializeRootTable();
        }

        System.out.println(vTable);

        try {
            instance = serializableClass.getDeclaredConstructor().newInstance();
            for (Field declaredField : instance.getClass().getDeclaredFields()) {
                if (vTable != null) {
                    VTableField vTableField = declaredField.getDeclaredAnnotation(VTableField.class);
                    short fieldOffset = vTable.getFieldOffset(vTableField.value());
                    if (fieldOffset <= 0) {  // Value is not present in this table
                        continue;
                    }

                    stream.seek(vTable.position() + fieldOffset);
                }
                handleField(instance, declaredField);
            }
        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    public <T> T deserializeRecord(Class<T> serializableRecord) {
        Constructor<T> suitableRecordConstructor = getSuitableRecordConstructor(serializableRecord);
        if (suitableRecordConstructor == null) {
            throw new IllegalStateException("Cannot find suitable constructor for record: " + serializableRecord.getSimpleName());
        }

        int parameterCount = suitableRecordConstructor.getParameterCount();

        int i = 0;
        Object[] parameters = new Object[parameterCount];
        for (Parameter parameter : suitableRecordConstructor.getParameters()) {
            parameters[i++] = deserializeParameter(parameter);
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

    private static boolean isPrimitive(Class<?> serializationClass) {
        return serializationClass.isPrimitive() || serializationClass == Long.class || serializationClass == Integer.class || serializationClass == Short.class || serializationClass == Byte.class || serializationClass == Boolean.class;
    }

    private static Class<?> getSerializationClass(Class<?> type, FlatType flatTypeAnnotation) {
        if (flatTypeAnnotation == null) return type;

        SerializeType flatType = flatTypeAnnotation.value();
        return flatType.getSerializationClass();
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
        if (primiviteClass == Long.class || primiviteClass == long.class) {
            //noinspection unchecked
            return (T) (Long) stream.readInt64();
        } else if (primiviteClass == Integer.class || primiviteClass == int.class) {
            //noinspection unchecked
            return (T) (Integer) stream.readInt32();
        } else if (primiviteClass == Short.class || primiviteClass == short.class) {
            //noinspection unchecked
            return (T) (Short) stream.readInt16();
        } else if (primiviteClass == Byte.class || primiviteClass == byte.class) {
            //noinspection unchecked
            return (T) (Byte) stream.readInt8();
        } else if (primiviteClass == Boolean.class || primiviteClass == boolean.class) {
            //noinspection unchecked
            return (T) (Boolean) stream.readBoolean();
        }

        throw new IllegalArgumentException("Provided type is not primitive! Provided class: " + primiviteClass.getSimpleName());
    }
}
