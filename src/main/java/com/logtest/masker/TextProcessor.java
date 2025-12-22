package com.logtest.masker;

import org.springframework.stereotype.Component;

import static com.logtest.masker.MaskPatternType.INN;
import static com.logtest.masker.MaskingConstantUtil.NONSTANDARD_VALUE_MASK;

@Component
public class TextProcessor extends AbstractMaskProcessor {

    @Override
    public Object process(Object value) {
        String trimmed = ((String) value).trim();
        return switch (trimmed.length()) {
            case 0 -> value;
            case 10, 12 -> maskInRange(trimmed, 2, 7);
            default -> NONSTANDARD_VALUE_MASK;
        };
    }

    @Override
    public MaskPatternType getType() {
        return INN;
    }
}
