package com.logtest.dto.dtoNumerical;

import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import com.logtest.masker.patterns.MaskPatternType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoNumerical {

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

    DtoNumerical dtoNumerical;

    private List<DtoNumerical> dtos;
}
