package com.vorono4ka.flatloader;

import com.vorono4ka.commons.streams.ByteStream;

public class FlatByteStream extends ByteStream {
    public FlatByteStream(byte[] bytes) {
        super(bytes);
    }

    public byte[] readInt8Array(int count) {
        byte[] array = new byte[count];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) this.readInt8();
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
