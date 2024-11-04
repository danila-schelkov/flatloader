package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.CustomStructureSize;
import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;
import com.vorono4ka.flatloader.swf.FloatRect;
import com.vorono4ka.flatloader.swf.matrix.ScMatrixBank;
import com.vorono4ka.flatloader.swf.movieclip.MovieClipFrameElement;
import com.vorono4ka.flatloader.swf.shape.ShapePoint;

import java.util.ArrayList;

@VTableClass
public class ResourceRoot {
    @VTableField(0)
    private ArrayList<String> strings;
    @VTableField(3)
    private ArrayList<FloatRect> scalingGrids;
    @VTableField(4)
    @CustomStructureSize(6)
    private ArrayList<MovieClipFrameElement> movieClipFrameElements;
    @VTableField(5)
    @CustomStructureSize(10)
    private ArrayList<ShapePoint> shapePoints;
    @VTableField(6)
    private ArrayList<ScMatrixBank> matrixBanks;
}
