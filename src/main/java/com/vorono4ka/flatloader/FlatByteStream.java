package com.vorono4ka.flatloader;

import com.vorono4ka.commons.streams.ByteStream;

import java.nio.charset.StandardCharsets;

public class FlatByteStream extends ByteStream {
    public FlatByteStream(byte[] bytes) {
        super(bytes);
    }
    
    public String readString() {
        int length = this.readInt32();
        if (length == 0) {
            return null;
        }

        byte[] bytes = this.read(length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public byte[] readInt8Array(int count) {
        byte[] array = new byte[count];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.readInt8();
        }

        return array;
    }

    public short[] readInt16Array(int count) {
        short[] array = new short[count];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.readInt16();
        }

        return array;
    }

    public int[] readInt32Array(int count) {
        int[] array = new int[count];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.readInt32();
        }

        return array;
    }
}
