package com.logtest.masker;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;


import static com.logtest.masker.MaskPatternType.OFFSET_DATE_TIME;
import static com.logtest.masker.MaskingConstantUtil.OFFSET_DATE_TIME_REPLACEMENT;

@Component
public class OffsetDateTimeProcessor extends AbstractMaskProcessor {

    @Override
    public Object process(Object value) {
        return value instanceof OffsetDateTime ? OFFSET_DATE_TIME_REPLACEMENT : value;
    }

    @Override
    public MaskPatternType getType() {
        return OFFSET_DATE_TIME;
    }

}
