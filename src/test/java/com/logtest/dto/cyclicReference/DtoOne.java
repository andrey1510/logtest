package com.logtest.dto.cyclicReference;

import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import com.logtest.masker.patterns.MaskPatternType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoOne {

    private String text;

    @MaskedProperty(type = MaskPatternType.TEXT_FIELD)
    private String textToBeMasked;

    private DtoTwo dtoTwo;
}
