package com.vorono4ka.flatloader.swf;

import com.vorono4ka.flatloader.annotations.FlatReference;
import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

import java.util.ArrayList;

@VTableClass
public class ExportRoot {
    @VTableField(0)
    private @FlatReference ArrayList<Short> exportIds;
    @VTableField(1)
    private @FlatReference ArrayList<Integer> exportNameIds;
}
