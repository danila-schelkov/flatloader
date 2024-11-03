package com.vorono4ka.flatloader.annotations;

import com.vorono4ka.flatloader.SerializeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlatReference {
    SerializeType value() default SerializeType.INT32;
}
