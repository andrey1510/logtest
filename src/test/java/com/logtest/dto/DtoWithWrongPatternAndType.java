package com.logtest.dto;

import com.logtest.masker.MaskPatternType;
import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoWithWrongPatternAndType {

    private boolean isMasked;

    @MaskedProperty(type = MaskPatternType.PIN)
    private String correctPatternAndType;

    @MaskedProperty(type = MaskPatternType.EMAIL)
    private String wrongPattern;

    @MaskedProperty(type = MaskPatternType.PIN)
    private Integer wrongType;

    private DtoWithWrongPatternAndType nestedDto;

}
