package com.vorono4ka.flatloader;

public enum SerializeType {
    INT8(Byte.class),
    INT16(Short.class),
    INT32(Integer.class),
    INT64(Long.class),
    UNSIGNED_INT8(Byte.class),
    UNSIGNED_INT16(Short.class),
    UNSIGNED_INT32(Integer.class),
    STRING(String.class),
    STRING_REFERENCE(Integer.class),
    ;

    private final Class<?> serializationClass;

    SerializeType(Class<?> serializationClass) {
        this.serializationClass = serializationClass;
    }

    public Class<?> getSerializationClass() {
        return serializationClass;
    }
}
