package com.logtest.masker.sample;


import com.logtest.masker.Masker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SampleMaskerController {

    private final Masker masker;

    @GetMapping("/mask")
    public ResponseEntity<SampleDto> getSampleDto() {

        log.info(" ----------- проверка маскировки DTO в логгере");
        SampleDto dto = SampleDto.builder()
            .isMasked(false)
            .innField("642125911478")
            .someDate(LocalDate.of(2001, 4, 3))
            .dateTime(OffsetDateTime.of(2023, 4, 4, 4, 4, 4, 4, ZoneOffset.UTC))
            .dtoList(List.of(new SampleDto(false, "842125911478", LocalDate.of(2012, 8, 8), OffsetDateTime.of(2024, 6, 5, 2, 2, 2, 3, ZoneOffset.UTC), null, null, "not to be masked")))
            .dtos(new SampleDto(false, "242125911472", LocalDate.of(2022, 1, 2), OffsetDateTime.of(2021, 5, 5, 4, 4, 4, 2, ZoneOffset.UTC), null, null, "not to be masked"))
            .notForMaskingField("not to be masked")
            .build();

        SampleDto maskedDto = masker.mask(dto);
        log.info("----masked Dto: " + maskedDto);

        return ResponseEntity.ok(dto);
    }

}
