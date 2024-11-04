package com.vorono4ka.flatloader;

public enum SerializeType {
    INT8(byte.class),
    INT16(short.class),
    INT32(int.class),
    INT64(long.class),
    STRING(String.class),
    STRING_REFERENCE(int.class);

    private final Class<?> serializationClass;

    SerializeType(Class<?> serializationClass) {
        this.serializationClass = serializationClass;
    }

    public Class<?> getSerializationClass() {
        return serializationClass;
    }
}
