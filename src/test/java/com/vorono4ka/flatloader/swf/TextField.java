package com.vorono4ka.flatloader.swf;

import com.vorono4ka.flatloader.annotations.DefaultValue;
import com.vorono4ka.flatloader.annotations.Offset;
import com.vorono4ka.flatloader.annotations.StructureSize;

@StructureSize(40)
public record TextField(
    @Offset(0) short id,
    @DefaultValue short unused,
    @Offset(4) int fontNameReferenceId,
    @Offset(8) Rect bounds,
    @Offset(16) int color,
    @Offset(20) int outlineColor,
    @Offset(24) int textReferenceId,
    @Offset(28) int anotherTextReferenceId,
    @Offset(32) byte styles,  // aka flags
    @Offset(33) byte align,
    @Offset(34) byte fontSize,
    // perhaps a short united with another short (then second is bend)
    @Offset(36) int unk
) {

}
