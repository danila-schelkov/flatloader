package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;
import com.vorono4ka.flatloader.swf.MovieClipModifier;

import java.util.ArrayList;

@VTableClass
public class MovieClipModifierRoot {
    @VTableField(0)
    private ArrayList<MovieClipModifier> modifiers;
}
