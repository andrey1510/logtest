package com.logtest.controllers;

import com.logtest.dto.CardDto;
import com.logtest.masker.annotations.EnableMasking;
import com.logtest.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.logtest.masker.Masker.mask;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @EnableMasking(false)
    @Operation(description = "пример номера карты - 1234567890123456")
    @PostMapping("/masked")
    public ResponseEntity<CardDto> createCard(@RequestBody CardDto cardDto) {

        log.info("проверка маскировки DTO в логгере - должна работать при указании @EnableMasking: {}",
            mask(cardDto));
        System.out.println("проверка маскировки DTO в sout - должна работать при указании @EnableMasking: " +
            mask(cardDto));
        log.info("проверка маскировки DTO в логгере - НЕ должна работать никогда {}", cardDto);
        System.out.println("проверка маскировки DTO в sout - НЕ должна работать никогда: " + cardDto);

        log.info("Контроллер - пришло DTO для сохранения: {}", mask(cardDto));

        CardDto savedDto = cardService.createCard(cardDto);
        log.info("Контроллер - пришло сохраненное DTO: {}", mask(savedDto));

        return ResponseEntity.ok(savedDto);
    }

}
