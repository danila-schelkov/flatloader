package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.CustomStructureSize;
import com.vorono4ka.flatloader.annotations.FlatReference;
import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;
import com.vorono4ka.flatloader.swf.FloatRect;
import com.vorono4ka.flatloader.swf.MovieClipFrameElement;
import com.vorono4ka.flatloader.swf.ShapePoint;

import java.util.ArrayList;

@VTableClass
public class ResourceRoot {
    @FlatReference
    @VTableField(0)
    private ArrayList<String> strings;
    @FlatReference
    @VTableField(3)
    private ArrayList<FloatRect> scalingGrids;
    @FlatReference
    @VTableField(4)
    @CustomStructureSize(6)
    private ArrayList<MovieClipFrameElement> movieClipFrameElements;
    @FlatReference
    @VTableField(5)
    @CustomStructureSize(10)
    private ArrayList<ShapePoint> shapePoints;
}
