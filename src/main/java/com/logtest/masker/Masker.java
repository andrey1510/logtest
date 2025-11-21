package com.logtest.masker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Masker {

    private static MaskingUtil maskingUtil;

    @Autowired
    public Masker(MaskingUtil maskingUtil) {
        Masker.maskingUtil = maskingUtil;
    }

    public static String mask(Object object) {
        return maskingUtil.maskDto(object);
    }
}
