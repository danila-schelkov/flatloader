package com.vorono4ka.flatloader;

public record VTable(int position, int dataSize, short[] fieldOffsets) {
    public short getFieldOffset(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldOffsets.length) {
            return Short.MIN_VALUE;
        }

        return fieldOffsets[fieldIndex];
    }
}
