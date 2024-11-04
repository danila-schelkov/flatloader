package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;
import com.vorono4ka.flatloader.swf.texture.TextureSet;

import java.util.ArrayList;

@VTableClass
public class TextureRoot {
    @VTableField(0)
    private ArrayList<TextureSet> textureSets;
}
