package com.vorono4ka.flatloader.swf;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

@VTableClass
public class TextureSet {
    @VTableField(0)
    private Texture lowresTexture;
    @VTableField(1)
    private Texture highresTexture;
}
