package com.vorono4ka.flatloader.swf.matrix;

import com.vorono4ka.flatloader.SerializeType;
import com.vorono4ka.flatloader.annotations.FlatType;

public class ColorTransform {
    @FlatType(value = SerializeType.INT8, isUnsigned = true)
    private int redAddition;
    @FlatType(value = SerializeType.INT8, isUnsigned = true)
    private int greenAddition;
    @FlatType(value = SerializeType.INT8, isUnsigned = true)
    private int blueAddition;
    @FlatType(value = SerializeType.INT8, isUnsigned = true)
    private int alpha;
    @FlatType(value = SerializeType.INT8, isUnsigned = true)
    private int redMultiplier;
    @FlatType(value = SerializeType.INT8, isUnsigned = true)
    private int greenMultiplier;
    @FlatType(value = SerializeType.INT8, isUnsigned = true)
    private int blueMultiplier;
}
