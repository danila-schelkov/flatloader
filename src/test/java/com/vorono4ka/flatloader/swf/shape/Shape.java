package com.vorono4ka.flatloader.swf.shape;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

import java.util.ArrayList;

@VTableClass
public class Shape {
    @VTableField(0)
    private short id;
    @VTableField(1)
    private ArrayList<ShapeDrawBitmapCommand> commands;
}
