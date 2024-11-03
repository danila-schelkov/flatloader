package com.vorono4ka.flatloader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
    long longValue() default 0;

    int intValue() default 0;

    short shortValue() default 0;

    byte byteValue() default 0;

    float floatValue() default 0;

    boolean booleanValue() default false;

    String stringValue() default "";
}
