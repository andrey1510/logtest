package com.logtest.masker;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class MaskingConstantUtil {

    public static final LocalDate LOCAL_DATE_REPLACEMENT = LocalDate.of(0, 1, 1);
    public static final OffsetDateTime OFFSET_DATE_TIME_REPLACEMENT = OffsetDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    public static final String NONSTANDARD_VALUE_MASK = "*****";

}
