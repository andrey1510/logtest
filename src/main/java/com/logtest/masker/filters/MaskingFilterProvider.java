package com.logtest.masker.filters;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaskingFilterProvider extends SimpleFilterProvider {

    private final MaskingPropertyFilter maskingPropertyFilter;

    @Override
    public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
        if ("maskingFilter".equals(filterId)) {
            return maskingPropertyFilter;
        }
        return super.findPropertyFilter(filterId, valueToFilter);
    }
}