package com.logtest.dto.dtoToString;

import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import com.logtest.masker.patterns.MaskPatternType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Masked
@Builder
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NestedDtoNoToString {

    @MaskedProperty(type = MaskPatternType.LOCAL_DATE)
    private LocalDate localDate;

    @MaskedProperty(type = MaskPatternType.OFFSET_DATE_TIME)
    private OffsetDateTime dateTime;

}
