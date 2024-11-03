package com.vorono4ka.flatloader.swf.matrix;

import com.vorono4ka.flatloader.annotations.FlatReference;
import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

import java.util.ArrayList;

@VTableClass
public class ScMatrixBank {
    @FlatReference
    @VTableField(0)
    private ArrayList<Matrix2x3> matrices;
    @FlatReference
    @VTableField(1)
    private ArrayList<ColorTransform> colorTransforms;
}
