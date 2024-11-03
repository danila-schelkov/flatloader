package com.vorono4ka.flatloader.swf;

public record TextField(
    short id,
    short unused,
    int fontNameReferenceId,
    Rect bounds,
    int color,
    int outlineColor,
    int textReferenceId,
    int anotherTextReferenceId,
    byte styles,  // aka flags
    byte align,
    byte fontSize,
    byte unused1,
    // perhaps a short united with another short (then second is bend)
    int unk
) {

}
