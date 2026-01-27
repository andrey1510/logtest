package com.logtest.testData;

import com.logtest.dto.dtoNumerical.DtoNumerical;
import com.logtest.dto.dtoNumerical.DtoNumericalNoToString;
import com.logtest.dto.dtoNumerical.WrongNumberTypeDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestDataForNumerical {

    private static final Long LONG_NUMBER = 8888L;
    private static final Long LONG_NUMBER_MASKED = 1111111111L;
    private static final Double DOUBLE_NUMBER = 8888.88;
    private static final Double DOUBLE_NUMBER_MASKED = 111111.11;
    private static final Integer INTEGER_NUMBER = 8888;
    private static final Integer INTEGER_NUMBER_MASKED = 1111111111;
    private static final BigDecimal BIG_DECIMAL_NUMBER = new BigDecimal("8888.88");
    private static final BigDecimal BIG_DECIMAL_NUMBER_MASKED = new BigDecimal("1111111111");
    private static final Short SHORT_NUMBER = 8888;

    public static final String DTO_NUMERICAL_MASKED = "DtoNumerical(noMaskNumber=666, integerNumber=***, longNumber=***, doubleNumber=***, BigDecimalNumber=***, integersList=[***], integersMap={1=***}, dtoNumerical=DtoNumerical(noMaskNumber=666, integerNumber=null, longNumber=***, doubleNumber=***, BigDecimalNumber=null, integersList=null, integersMap=null, dtoNumerical=null, dtos=null), dtos=[DtoNumerical(noMaskNumber=666, integerNumber=null, longNumber=***, doubleNumber=***, BigDecimalNumber=null, integersList=null, integersMap=null, dtoNumerical=null, dtos=null)])";
    public static final String DTO_NUMERICAL_NO_TOSTRING_MASKED = "DtoNumericalNoToString(noMaskNumber=666, integerNumber=***, longNumber=***, doubleNumber=***, BigDecimalNumber=***, integersList=[***], integersMap={1=***}, dtoNumerical=DtoNumericalNoToString(noMaskNumber=666, integerNumber=null, longNumber=***, doubleNumber=***, BigDecimalNumber=null, integersList=null, integersMap=null, dtoNumerical=null, dtos=null), dtos=[DtoNumericalNoToString(noMaskNumber=666, integerNumber=null, longNumber=***, doubleNumber=***, BigDecimalNumber=null, integersList=null, integersMap=null, dtoNumerical=null, dtos=null)])";


    protected DtoNumerical createDtoNumericalNested() {
        return DtoNumerical.builder()
            .noMaskNumber(666)
            .doubleNumber(DOUBLE_NUMBER)
            .integerNumber(null)
            .longNumber(LONG_NUMBER)
            .BigDecimalNumber(null)
            .build();
    }

    protected DtoNumerical createDtoNumerical() {
        return DtoNumerical.builder()
            .noMaskNumber(666)
            .doubleNumber(DOUBLE_NUMBER)
            .integerNumber(INTEGER_NUMBER)
            .longNumber(LONG_NUMBER)
            .dtos(new ArrayList<>(List.of(createDtoNumericalNested())))
            .BigDecimalNumber(BIG_DECIMAL_NUMBER)
            .dtoNumerical(createDtoNumericalNested())
            .integersList(new ArrayList<>(List.of(INTEGER_NUMBER)))
            .integersMap(new HashMap<>() {{
                put(1, INTEGER_NUMBER);
            }})
            .build();
    }

    protected DtoNumerical createDtoNumericalNestedMasked() {
        return DtoNumerical.builder()
            .noMaskNumber(666)
            .doubleNumber(DOUBLE_NUMBER_MASKED)
            .integerNumber(null)
            .longNumber(LONG_NUMBER_MASKED)
            .BigDecimalNumber(null)
            .build();
    }

    protected DtoNumerical createDtoNumericalMasked() {
        return DtoNumerical.builder()
            .noMaskNumber(666)
            .doubleNumber(DOUBLE_NUMBER_MASKED)
            .integerNumber(INTEGER_NUMBER_MASKED)
            .longNumber(LONG_NUMBER_MASKED)
            .dtos(new ArrayList<>(List.of(createDtoNumericalNestedMasked())))
            .BigDecimalNumber(BIG_DECIMAL_NUMBER_MASKED)
            .dtoNumerical(createDtoNumericalNestedMasked())
            .integersList(new ArrayList<>(List.of(INTEGER_NUMBER_MASKED)))
            .integersMap(new HashMap<>() {{
                put(1, INTEGER_NUMBER_MASKED);
            }})
            .build();
    }

    protected WrongNumberTypeDto createWrongNumberTypeDto() {
        return WrongNumberTypeDto.builder()
            .shortNumber(SHORT_NUMBER)
            .build();
    }

    protected DtoNumericalNoToString createDtoNumericalNoToStringNested() {
        return DtoNumericalNoToString.builder()
            .noMaskNumber(666)
            .doubleNumber(DOUBLE_NUMBER)
            .integerNumber(null)
            .longNumber(LONG_NUMBER)
            .BigDecimalNumber(null)
            .build();
    }

    protected DtoNumericalNoToString createDtoNumericalNoToString() {
        return DtoNumericalNoToString.builder()
            .noMaskNumber(666)
            .doubleNumber(DOUBLE_NUMBER)
            .integerNumber(INTEGER_NUMBER)
            .longNumber(LONG_NUMBER)
            .dtos(new ArrayList<>(List.of(createDtoNumericalNoToStringNested())))
            .BigDecimalNumber(BIG_DECIMAL_NUMBER)
            .dtoNumerical(createDtoNumericalNoToStringNested())
            .integersList(new ArrayList<>(List.of(INTEGER_NUMBER)))
            .integersMap(new HashMap<>() {{
                put(1, INTEGER_NUMBER);
            }})
            .build();
    }

}
