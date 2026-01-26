package com.logtest;

import com.logtest.masker.Masker;
import com.logtest.testData.TestDataForNumerical;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaskerNumericalTests extends TestDataForNumerical {

    @Test
    void mask_testNumerical() {
        assertEquals(createDtoNumericalMasked(), Masker.mask(createDtoNumerical()));
        assertEquals(createWrongNumberTypeDto(), Masker.mask(createWrongNumberTypeDto()));
    }

    @Test
    void maskToString_testNumerical() {
        assertEquals(DTO_NUMERICAL_MASKED, Masker.maskToString(createDtoNumerical()));
    }

}
