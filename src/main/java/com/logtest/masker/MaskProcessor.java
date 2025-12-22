package com.logtest.masker;

public interface MaskProcessor {

    Object process(Object value);

    MaskPatternType getType();

}
