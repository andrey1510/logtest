package com.logtest.dto.nestedDto;

import com.logtest.masker.MaskPatternType;
import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdDocument {

    private boolean isMasked;

    @MaskedProperty(type = MaskPatternType.OTHER_DUL_SERIES)
    private String otherDulSeries;

    @MaskedProperty(type = MaskPatternType.OTHER_DUL_NUMBER)
    private String otherDulNumber;

    @MaskedProperty(type = MaskPatternType.LOCALDATE_TO_ZERO_DATE)
    private LocalDate someDate;

}
