package com.logtest.masker;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.logtest.masker.MaskPatternType.LOCAL_DATE;
import static com.logtest.masker.MaskingConstantUtil.LOCAL_DATE_REPLACEMENT;

//ToDo - check with temporal. м.б. единый класс для LocalDate and OffsetDateTime
@Component
public class LocalDateProcessor extends AbstractMaskProcessor  {

    @Override
    public Object process(Object value) {
        return value instanceof LocalDate ? LOCAL_DATE_REPLACEMENT : value;
    }

    @Override
    public MaskPatternType getType() {
        return LOCAL_DATE;
    }
}
