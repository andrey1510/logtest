package com.logtest.dto.dtoForCollection;

import com.logtest.masker.MaskType;
import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionElement {

    boolean isMasked;

    @MaskedProperty(type = MaskType.PHONE)
    private String phoneNumber;
}
