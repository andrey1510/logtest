package com.logtest.testData;

import com.logtest.dto.dtoForCollection.CollectionDtoElement;
import com.logtest.dto.dtoToString.NestedDto;
import com.logtest.dto.dtoToString.NestedDtoNoToStringOverride;
import com.logtest.dto.dtoToString.UpperLevelDto;
import com.logtest.dto.dtoToString.UpperLevelDtoNoToStringOverride;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TestDataForToString {

    private static final String DATES_NOT_TO_BE_MASKED_TEXT = "=0001 :0001 112.0001 12.0001 0001-12 11.11.2001 2001-11-11 shall not to be masked";
    private static final LocalDate DATE_1 = LocalDate.of(2023, 5, 2);
    private static final LocalDate DATE_1_MASKED = LocalDate.of(0, 5, 2);
    private static final LocalDate DATE_2 = LocalDate.of(2024, 4, 3);
    private static final LocalDate DATE_2_MASKED = LocalDate.of(0, 4, 3);
    private static final OffsetDateTime DATE_TIME_1 = OffsetDateTime.of(0, 12, 31, 23, 59, 59, 60000, ZoneOffset.UTC);
    private static final OffsetDateTime DATE_TIME_1_MASKED = OffsetDateTime.of(0, 4, 4, 4, 4, 4, 4, ZoneOffset.UTC);
    private static final OffsetDateTime DATE_TIME_2 = OffsetDateTime.of(2025, 4, 5, 6, 4, 4, 4, ZoneOffset.UTC);
    private static final OffsetDateTime DATE_TIME_2_MASKED = OffsetDateTime.of(0, 4, 5, 6, 4, 4, 4, ZoneOffset.UTC);


    protected NestedDto createNestedDto1() {
        return NestedDto.builder()
            .localDate(DATE_1)
            .dateTime(DATE_TIME_1)
            .build();
    }

    protected NestedDto createNestedDto2() {
        return NestedDto.builder()
            .localDate(DATE_2)
            .dateTime(DATE_TIME_2)
            .build();
    }

    protected UpperLevelDto createUpperLevelDto() {
        return UpperLevelDto.builder()
            .localDate(DATE_1)
            .offsetDateTime(DATE_TIME_1)
            .nestedDto(createNestedDto1())
            .arrayWithDtos(new NestedDto[]{createNestedDto1(), createNestedDto2()})
            .listWithDtos(new ArrayList<>(List.of(createNestedDto1(), createNestedDto2())))
            .setWithDtos(new HashSet<>(Set.of(createNestedDto1(), createNestedDto2())))
            .mapWithDtos(new HashMap<>() {{
                put("key1", createNestedDto1());
                put("key2", createNestedDto2());
            }})
            .setWithLocalDates(new HashSet<>(Set.of(DATE_1, DATE_2)))
            .setWithOffsetDateTime(new HashSet<>(Set.of(DATE_TIME_1, DATE_TIME_2)))
            .listWithOffsetDateTime(new ArrayList<>(List.of(DATE_TIME_1, DATE_TIME_2)))
            .listWithLocalDates(new ArrayList<>(List.of(DATE_1, DATE_2)))
            .mapWithLocalDates(new HashMap<>() {{
                put("key1", DATE_1);
                put("key2", DATE_2);
            }})
            .mapWithOffsetDateTime(new HashMap<>() {{
                put("key1", DATE_TIME_1);
                put("key2", DATE_TIME_2);
            }})
            .build();
    }

    protected NestedDtoNoToStringOverride createNestedDtoNoToStringOverride1() {
        return NestedDtoNoToStringOverride.builder()
            .localDate(DATE_1)
            .dateTime(DATE_TIME_1)
            .build();
    }

    protected NestedDtoNoToStringOverride createNestedDtoNoToStringOverride2() {
        return NestedDtoNoToStringOverride.builder()
            .localDate(DATE_2)
            .dateTime(DATE_TIME_2)
            .build();
    }

    protected UpperLevelDtoNoToStringOverride createUpperLevelDtoNoToStringOverride() {
        return UpperLevelDtoNoToStringOverride.builder()
            .localDate(DATE_1)
            .offsetDateTime(DATE_TIME_1)
            .nestedDto(createNestedDtoNoToStringOverride1())
            .arrayWithDtos(new NestedDtoNoToStringOverride[]{createNestedDtoNoToStringOverride1(), createNestedDtoNoToStringOverride2()})
            .listWithDtos(new ArrayList<>(List.of(createNestedDtoNoToStringOverride1(), createNestedDtoNoToStringOverride2())))
            .setWithDtos(new HashSet<>(Set.of(createNestedDtoNoToStringOverride1(), createNestedDtoNoToStringOverride2())))
            .mapWithDtos(new HashMap<>() {{
                put("key1", createNestedDtoNoToStringOverride1());
                put("key2", createNestedDtoNoToStringOverride2());
            }})
            .setWithLocalDates(new HashSet<>(Set.of(DATE_1, DATE_2)))
            .setWithOffsetDateTime(new HashSet<>(Set.of(DATE_TIME_1, DATE_TIME_2)))
            .listWithOffsetDateTime(new ArrayList<>(List.of(DATE_TIME_1, DATE_TIME_2)))
            .listWithLocalDates(new ArrayList<>(List.of(DATE_1, DATE_2)))
            .mapWithLocalDates(new HashMap<>() {{
                put("key1", DATE_1);
                put("key2", DATE_2);
            }})
            .mapWithOffsetDateTime(new HashMap<>() {{
                put("key1", DATE_TIME_1);
                put("key2", DATE_TIME_2);
            }})
            .build();
    }

}
