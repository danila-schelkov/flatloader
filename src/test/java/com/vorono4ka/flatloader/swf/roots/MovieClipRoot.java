package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;
import com.vorono4ka.flatloader.swf.movieclip.MovieClip;

import java.util.ArrayList;

@VTableClass
public class MovieClipRoot {
    @VTableField(0)
    private ArrayList<MovieClip> movieClips;
}
