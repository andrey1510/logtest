package com.logtest.dto.dtoNumerical;

import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import com.logtest.masker.patterns.MaskPatternType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Masked
@Builder
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoNumericalNoToString {

    private Integer noMaskNumber;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private Integer integerNumber;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private Long longNumber;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private Double doubleNumber;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private BigDecimal BigDecimalNumber;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private List<Integer> integersList;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private Map<Integer, Integer> integersMap;

    DtoNumericalNoToString dtoNumerical;

    private List<DtoNumericalNoToString> dtos;
}
