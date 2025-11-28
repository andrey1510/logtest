package com.logtest.dto.dtoForCollection;

import com.logtest.masker.MaskPatternType;
import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoWithMap {

    boolean isMasked;

    @MaskedProperty(type = MaskPatternType.TEXT_FIELD)
    private String textField;

    private Map<String, CollectionElement> dtos;
}
