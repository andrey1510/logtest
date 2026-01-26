package com.logtest.dto.dtoNumerical;

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
public class WrongNumberTypeDto {

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private Short shortNumber;
}
