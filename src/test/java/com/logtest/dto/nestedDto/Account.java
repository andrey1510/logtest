package com.logtest.dto.nestedDto;

import com.logtest.masker.MaskPatternType;
import com.logtest.masker.annotations.Masked;
import com.logtest.masker.annotations.MaskedProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private boolean isMasked;

    @MaskedProperty(type = MaskPatternType.PAN)
    private String pan;

    @MaskedProperty(type = MaskPatternType.BALANCE)
    private String balance;

    Set<Credentials> credentials;

}
