package com.logtest.masker;


public abstract class AbstractMaskProcessor implements MaskProcessor {

    protected String maskInRange(String str, int start, int end) {
        char[] chars = str.toCharArray();
        for (int i = start; i < end; i++) {
            chars[i] = '*';
        }
        return new String(chars);
    }

}
