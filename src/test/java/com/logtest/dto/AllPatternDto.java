package com.logtest.dto;

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
public class AllPatternDto {

    private boolean isMasked;

    @MaskedProperty(type = MaskPatternType.NAME)
    private String fullname;

    @MaskedProperty(type = MaskPatternType.EMAIL)
    private String email;

    @MaskedProperty(type = MaskPatternType.PHONE)
    private String phoneNumber;

    @MaskedProperty(type = MaskPatternType.TEXT_FIELD)
    private String textField;

    @MaskedProperty(type = MaskPatternType.PAN)
    private String pan;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private String balance;

    @MaskedProperty(type = MaskPatternType.CONFIDENTIAL_NUMBER)
    private String confidentialNumber;

    @MaskedProperty(type = MaskPatternType.PIN)
    private String pin;

    @MaskedProperty(type = MaskPatternType.OTHER_DUL_SERIES)
    private String otherDulSeries;

    @MaskedProperty(type = MaskPatternType.OTHER_DUL_NUMBER)
    private String otherDulNumber;

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

    @MaskedProperty(type = MaskPatternType.LOCALDATE_TO_ZERO_DATE)
    private LocalDate someDate;

    private String issuanceDateMasked;

    private String notForMaskingField;
}
