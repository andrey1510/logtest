package com.logtest.dto;

import com.logtest.masker.Masked;
import com.logtest.masker.MaskedProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Masked
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    @MaskedProperty(pattern = "(\\d{6})\\d+(\\d{4})", replacement = "$1***$2")
    private String cardNumber;

    @MaskedProperty(pattern = "(\\d{3})\\d{10}(\\d{3})", replacement = "$1**********$2")
    private String cardAnotherNumber;

    private String cardDescription; // Won't be masked

}
