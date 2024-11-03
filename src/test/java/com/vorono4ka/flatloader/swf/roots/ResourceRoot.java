package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.FlatReference;
import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

import java.util.ArrayList;

@VTableClass
public class ResourceRoot {
    @FlatReference
    @VTableField(0)
    private ArrayList<String> strings;
}
