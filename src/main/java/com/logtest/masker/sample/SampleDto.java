package com.logtest.masker.sample;

import com.logtest.masker.MaskPatternType;
import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleDto {

    private boolean isMasked;

    @MaskedProperty(type = MaskPatternType.INN)
    private String innField;

    @MaskedProperty(type = MaskPatternType.LOCAL_DATE)
    private LocalDate someDate;

    @MaskedProperty(type = MaskPatternType.OFFSET_DATE_TIME)
    private OffsetDateTime dateTime;

    private SampleDto dtos;

    private List<SampleDto> dtoList;

    private String notForMaskingField;

}
