package com.vorono4ka.flatloader.swf.matrix;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;

import java.util.ArrayList;

@VTableClass
public class ScMatrixBank {
    @VTableField(0)
    private ArrayList<Matrix2x3> matrices;
    @VTableField(1)
    private ArrayList<ColorTransform> colorTransforms;
}
