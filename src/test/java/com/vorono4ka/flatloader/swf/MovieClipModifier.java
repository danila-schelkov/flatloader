package com.vorono4ka.flatloader.swf;

import com.vorono4ka.flatloader.SerializeType;
import com.vorono4ka.flatloader.annotations.FlatType;

public record MovieClipModifier(
    @FlatType(value = SerializeType.INT16, isUnsigned = true) int id,
    @FlatType(value = SerializeType.INT16, isUnsigned = true) int type) {
}
