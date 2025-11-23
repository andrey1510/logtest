package com.logtest.services;

import com.logtest.dto.CardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.logtest.masker.MaskingUtils.mask;


@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    public CardDto createCard(CardDto cardDto, boolean needMasking) {

        if (needMasking) {
            CardDto maskedDto = mask(cardDto);
            log.info("Masked Dto in logger: {}", maskedDto);
            return maskedDto;
        } else {
            log.info("Not masked Dto in logger: {}", cardDto);
            return cardDto;
        }
    }
}
