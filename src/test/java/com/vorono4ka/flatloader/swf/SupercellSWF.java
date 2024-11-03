package com.vorono4ka.flatloader.swf;

import com.vorono4ka.flatloader.annotations.FlatChunk;
import com.vorono4ka.flatloader.swf.roots.*;

public class SupercellSWF {
    @FlatChunk
    private ResourceRoot resourceRoot;
    @FlatChunk
    private ExportRoot exportRoot;
    @FlatChunk
    private TextFieldRoot textFieldRoot;
    @FlatChunk
    private ShapeRoot shapeRoot;
    @FlatChunk
    private MovieClipRoot movieClipRoot;
    @FlatChunk
    private MovieClipModifierRoot movieClipModifierRoot;
    @FlatChunk
    private TextureRoot textureRoot;
}
