package com.logtest.masker.annotations;

import com.logtest.masker.utils.MaskPatternType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskedProperty {
    MaskPatternType type() default MaskPatternType.CUSTOM;
    String pattern() default "";
    String replacement() default "";
}
