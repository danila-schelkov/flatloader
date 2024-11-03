package com.vorono4ka.flatloader.swf;

import com.vorono4ka.flatloader.SerializeType;
import com.vorono4ka.flatloader.annotations.FlatType;

public record MovieClipModifier(@FlatType(SerializeType.UNSIGNED_INT16) int id,
                                @FlatType(SerializeType.UNSIGNED_INT16) int type) {
}
