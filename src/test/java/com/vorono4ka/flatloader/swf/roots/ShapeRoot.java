package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.FlatReference;
import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;
import com.vorono4ka.flatloader.swf.shape.Shape;

import java.util.ArrayList;

@VTableClass
public class ShapeRoot {
    @VTableField(0)
    private @FlatReference ArrayList<Shape> shapes;
}
