package com.logtest.services;

import com.logtest.dto.CardDto;
import com.logtest.entities.CardEntity;
import com.logtest.masker.annotations.EnableMasking;
import com.logtest.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.logtest.masker.Masker.mask;


@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    @EnableMasking(false)
    public CardDto createCard(CardDto cardDto) {
        log.info("Сервис - пришло Dto из контроллера: {}",mask(cardDto));

        CardDto editedCardDto = new CardDto(cardDto.getCardNumber(), cardDto.getDescription() + " add text");
        log.info("Сервис - симуляция действий с dto: {}", mask(editedCardDto));

        CardEntity cardEntity = new CardEntity(1, editedCardDto.getCardNumber(), editedCardDto.getDescription());
        log.info("Сервис - создано entity (НЕ должно маскироваться): {}", cardEntity);

        CardEntity savedCard = cardRepository.emulateSave(cardEntity);
        CardDto savedCardDto = new CardDto(savedCard.getCardNumber(), savedCard.getDescription());
        log.info("Сервис - сохраненное entity конвертировано в DTO: {}", mask(savedCardDto));

        return savedCardDto;

    }
}