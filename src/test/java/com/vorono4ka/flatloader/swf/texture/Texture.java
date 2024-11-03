package com.vorono4ka.flatloader.swf.texture;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

@VTableClass
public class Texture {
    @VTableField(0)
    private int flags;
    @VTableField(1)
    private byte type;
    @VTableField(2)
    private short width;
    @VTableField(3)
    private short height;
    @VTableField(4)
    private int byteArrayPtr;
    @VTableField(5)
    private int textureFilenameId;
}
