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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Masked
@Builder
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoNoToString {

    @MaskedProperty(type = MaskPatternType.LOCAL_DATE)
    private LocalDate localDate;

    @MaskedProperty(type = MaskPatternType.OFFSET_DATE_TIME)
    private OffsetDateTime offsetDateTime;

    private NestedDtoNoToString nestedDto;

    private List<NestedDtoNoToString> listWithDtos;

    private Map<String, NestedDtoNoToString> mapWithDtos;

    private Set<NestedDtoNoToString> setWithDtos;

    private NestedDtoNoToString[] arrayWithDtos;

    @MaskedProperty(type = MaskPatternType.LOCAL_DATE)
    private List<LocalDate> listWithLocalDates;

    @MaskedProperty(type = MaskPatternType.LOCAL_DATE)
    private Set<LocalDate> setWithLocalDates;

    @MaskedProperty(type = MaskPatternType.LOCAL_DATE)
    private Map<String, LocalDate> mapWithLocalDates;

    @MaskedProperty(type = MaskPatternType.OFFSET_DATE_TIME)
    private List<OffsetDateTime> listWithOffsetDateTime;

    @MaskedProperty(type = MaskPatternType.OFFSET_DATE_TIME)
    private Set<OffsetDateTime> setWithOffsetDateTime;

    @MaskedProperty(type = MaskPatternType.OFFSET_DATE_TIME)
    private Map<String, OffsetDateTime> mapWithOffsetDateTime;

}
