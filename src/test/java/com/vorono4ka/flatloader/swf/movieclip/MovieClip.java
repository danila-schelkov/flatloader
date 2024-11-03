package com.vorono4ka.flatloader.swf.movieclip;

import com.vorono4ka.flatloader.annotations.FlatReference;
import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

import java.util.ArrayList;

@VTableClass
public class MovieClip {
    @VTableField(0)
    private short id;
    @VTableField(1)
    private int exportNameRefId;
    @VTableField(2)
    private byte fps;
    @VTableField(3)
    private short frameCount;
    @VTableField(4)
    private boolean unk;
    @VTableField(5)
    private @FlatReference ArrayList<Short> childIds;
    @VTableField(6)
    private @FlatReference ArrayList<Byte> childBlends;
    @VTableField(7)
    private @FlatReference ArrayList<Short> childNames;
    @VTableField(8)
    private @FlatReference ArrayList<MovieClipFrame> frames;
    @VTableField(9)
    private int frameElementOffset;
    @VTableField(10)
    private short matrixBankIndex;
    @VTableField(11)
    private int scalingGridIndex;
}
