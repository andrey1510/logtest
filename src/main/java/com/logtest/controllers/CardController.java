package com.logtest.controllers;

import com.logtest.dto.CardDto;
import com.logtest.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @Operation(
        summary = "Создание карты с опциональной маскировкой",
        description = "Пример номера карты - 1234567890123456. Параметр needMasking управляет маскировкой данных"
    )
    @PostMapping("/masked")
    public ResponseEntity<CardDto> createCard(
        @RequestBody CardDto cardDto,
        @Parameter(description = "Флаг маскировки данных. Если true - чувствительные данные будут замаскированы")
        @RequestParam(defaultValue = "true") boolean needMasking)
    {
        return ResponseEntity.ok(cardService.createCard(cardDto, needMasking));
    }
}
