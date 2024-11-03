package com.vorono4ka.commons.streams;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteStream {
    private static final int DEFAULT_BUFFER_LENGTH = 16;

    protected byte[] data;
    protected int position;

    public ByteStream() {
        this(DEFAULT_BUFFER_LENGTH);
    }

    public ByteStream(int initialLength) {
        this(new byte[initialLength]);
    }

    public ByteStream(byte[] data) {
        this.setData(data);
    }

    public boolean isAtAnd() {
        return this.position >= this.data.length;
    }

    public void ensureCapacity(int count) {
        int capacity = this.position + count;
        if (this.data.length < capacity) {
            int newSize = (int) (this.data.length * 1.5f);
            if (newSize < capacity) {
                newSize = capacity;
            }

            this.data = Arrays.copyOf(this.data, newSize);
        }
    }

    public int tell() {
        return this.position;
    }

    public void seek(int position) {
        this.position = position;
    }

    public void write(byte... data) {
        this.ensureCapacity(data.length);

        System.arraycopy(data, 0, this.data, this.position, data.length);
        this.position += data.length;
    }

    public byte[] read(int length) {
        byte[] data = new byte[length];
        if (length <= this.data.length - this.position) {
            System.arraycopy(this.data, this.position, data, 0, length);
            this.position += length;
        }

        return data;
    }

    public void skip(int length) {
        this.position += length;
    }

    public void writeInt8(int value) {
        this.ensureCapacity(1);

        this.write((byte) value);
    }

    public void writeInt16(int value) {
        this.ensureCapacity(2);

        this.write((byte) (value & 0xFF));
        this.write((byte) ((value >> 8) & 0xFF));
    }

    public void writeInt32(int value) {
        this.ensureCapacity(4);

        this.write((byte) (value & 0xFF));
        this.write((byte) ((value >> 8) & 0xFF));
        this.write((byte) ((value >> 16) & 0xFF));
        this.write((byte) ((value >> 24) & 0xFF));
    }

    public void writeInt64(long value) {
        this.ensureCapacity(8);

        this.write((byte) (value & 0xFF));
        this.write((byte) ((value >> 8) & 0xFF));
        this.write((byte) ((value >> 16) & 0xFF));
        this.write((byte) ((value >> 24) & 0xFF));
        this.write((byte) ((value >> 32) & 0xFF));
        this.write((byte) ((value >> 40) & 0xFF));
        this.write((byte) ((value >> 48) & 0xFF));
        this.write((byte) ((value >> 56) & 0xFF));
    }

    public void writeFloat(float value) {
        this.ensureCapacity(4);

        this.writeInt32(Float.floatToIntBits(value));
    }

    public void writeBoolean(boolean value) {
        this.writeInt8(value ? 1 : 0);
    }

    public byte readInt8() {
        return (byte) (this.data[this.position++] & 0xFF);
    }

    public short readInt16() {
        return (short) (this.readInt8() & 0xFF | (this.readInt8() & 0xFF) << 8);
    }

    public int readInt32() {
        return (this.readInt8() & 0xFF) | (this.readInt8() & 0xFF) << 8 | (this.readInt8() & 0xFF) << 16 | (this.readInt8() & 0xFF) << 24;
    }

    public long readInt64() {
        return (this.readInt8() & 0xFF) | (long) (this.readInt8() & 0xFF) << 8 | (long) (this.readInt8() & 0xFF) << 16 | (long) (this.readInt8() & 0xFF) << 24 | (long) (this.readInt8() & 0xFF) << 32 | (long) (this.readInt8() & 0xFF) << 40 | (long) (this.readInt8() & 0xFF) << 48 | (long) (this.readInt8() & 0xFF) << 56;
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt32());
    }

    public boolean readBoolean() {
        return this.readInt8() == 1;
    }

    public byte[] getData() {
        return ByteBuffer.allocate(this.position).put(this.data, 0, this.position).array();
    }

    public void setData(byte[] data) {
        this.data = data;
        this.position = 0;
    }
}