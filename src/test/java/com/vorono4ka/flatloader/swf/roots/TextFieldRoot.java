package com.vorono4ka.flatloader.swf.roots;

import com.vorono4ka.flatloader.annotations.VTableClass;
import com.vorono4ka.flatloader.annotations.VTableField;
import com.vorono4ka.flatloader.swf.textfield.TextField;

import java.util.ArrayList;

@VTableClass
public class TextFieldRoot {
    @VTableField(0)
    private ArrayList<TextField> textFields;
}
