package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

import java.util.ArrayList;

@VTableClass
public class ExportRoot {
    @VTableField(0)
    private ArrayList<Short> exportIds;
    @VTableField(1)
    private ArrayList<Integer> exportNameIds;
}
