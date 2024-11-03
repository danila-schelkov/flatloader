package com.vorono4ka.flatloader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes structure size in bytes, ignored when used with VTableClass
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StructureSize {
    /**
     * @return field offset index in structure, measured in bytes.
     */
    int value();
}
