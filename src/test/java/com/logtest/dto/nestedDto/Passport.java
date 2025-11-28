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
public class Passport {

    private boolean isMasked;

    @MaskedProperty(type = MaskPatternType.PASSPORT)
    private String passport;

    @MaskedProperty(type = MaskPatternType.PASSPORT_SERIES)
    private String passportSeries;

    @MaskedProperty(type = MaskPatternType.PASSPORT_NUMBER)
    private String passportNumber;

    @MaskedProperty(type = MaskPatternType.ISSUER_CODE)
    private String issuerCode;

    @MaskedProperty(type = MaskPatternType.ISSUER_NAME)
    private String issuerName;

    @MaskedProperty(type = MaskPatternType.LOCALDATE_FIELD_TO_STRING_FIELD)
    private LocalDate issuanceDate;

    private String issuanceDateMasked;
}
