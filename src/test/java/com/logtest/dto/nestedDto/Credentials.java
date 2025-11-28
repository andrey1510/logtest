package com.logtest.dto.nestedDto;

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
public class Credentials {

    private boolean isMasked;

    @MaskedProperty(type = MaskPatternType.CONFIDENTIAL_NUMBER)
    private String confidentialNumber;

    @MaskedProperty(type = MaskPatternType.PIN)
    private String pin;

}
