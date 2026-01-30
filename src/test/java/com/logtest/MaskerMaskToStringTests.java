package com.logtest;

import com.logtest.dto.cyclicReference.DtoOne;
import com.logtest.dto.cyclicReference.DtoThree;
import com.logtest.dto.cyclicReference.DtoTwo;
import com.logtest.masker.Masker;
import com.logtest.testData.TestDataForToString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaskerMaskToStringTests extends TestDataForToString {

    @Test
    void maskToString_test() {
        assertEquals(DTO_MASKED, Masker.maskToString(createDto()));
    }

    @Test
    void maskToStringWithOverride_test() {
        assertEquals(DTO_MASKED, Masker.maskToStringWithOverride(createDto()));
        assertEquals(DTO_NO_TO_STRING_MASKED, Masker.maskToStringWithOverride(createDtoNoToString()));
    }

    @Test
    void maskToString_testFlaws() {
        assertEquals(DTO_WITH_FLAWS_MASKED, Masker.maskToString(createDtoWithFlaws()));
    }

    @Test
    void maskToStringWithOverride_testFlaws() {
        assertEquals(DTO_WITH_FLAWS_MASKED, Masker.maskToStringWithOverride(createDtoWithFlaws()));
    }

    @Test
    void maskToStringWithOverride_testOverride() {
        assertEquals(DTO_TO_STRING_OVERRIDE, Masker.maskToStringWithOverride(createDtoToStringOverride()));
    }


    @Test
    void maskToString_testCyclicReference() {
        DtoThree dtoThree = new DtoThree("dto3", "dto3", null);
        DtoTwo dtoTwo = new DtoTwo("dto2", "dto2", dtoThree);
        DtoOne dtoOne = new DtoOne("dto1", "dto1", dtoTwo);
        dtoThree.setDtoOne(dtoOne);

        String expected = "DtoOne(text=dto1, textToBeMasked=d*****, dtoTwo=DtoTwo(text=dto2, textToBeMasked=d*****, " +
            "dtoOne=[cyclic reference error]))";

        assertEquals(expected, Masker.maskToString(dtoOne));

    }

    @Test
    void maskToStringWithOverride_testCyclicReference() {
        DtoThree dtoThree = new DtoThree("dto3", "dto3", null);
        DtoTwo dtoTwo = new DtoTwo("dto2", "dto2", dtoThree);
        DtoOne dtoOne = new DtoOne("dto1", "dto1", dtoTwo);
        dtoThree.setDtoOne(dtoOne);

        String expected = "DtoOne(text=dto1, textToBeMasked=d*****, dtoTwo=DtoTwo(text=dto2, textToBeMasked=d*****, " +
            "dtoThree=DtoThree(text=dto3, textToBeMasked=d*****, dtoOne=[cyclic reference error])))";

        assertEquals(expected, Masker.maskToStringWithOverride(dtoOne));
    }

    @Test
    void mask_testCyclicReference() {
        DtoOne dtoOne = new DtoOne("dto1", "dto1", null);
        DtoTwo dtoTwo = new DtoTwo("dto2", "dto2", null);
        DtoThree dtoThree = new DtoThree("dto3", "dto3", null);
        dtoOne.setDtoTwo(dtoTwo);
        dtoTwo.setDtoThree(dtoThree);
        dtoThree.setDtoOne(dtoOne);

        String expected = "DtoOne(text=dto1, textToBeMasked=d*****, dtoTwo=DtoTwo(text=dto2, textToBeMasked=d*****, " +
            "dtoOne=[cyclic reference error]))";

        DtoOne mask = Masker.mask(dtoOne);
        System.out.println(mask.getTextToBeMasked());


        //assertEquals(expected, mask);
    }


}
