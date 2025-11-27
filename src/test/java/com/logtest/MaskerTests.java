package com.logtest;

import com.logtest.masker.Masker;
import com.logtest.testData.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaskerTests extends TestData {

    @Test
    void testAllFields() {
        assertEquals(createAllPatternDtoMasked(), Masker.mask(createAllPatternDto()));
    }

    @Test
    void testNestedFields() {
        assertEquals(createPersonMasked(), Masker.mask(createPerson()));
    }

}