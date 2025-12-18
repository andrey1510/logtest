package com.logtest.dto.dtoForCollection;

import com.logtest.masker.annotations.Masked;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Masked
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerForDtoWithCollectionsRegularElements {

    private Boolean isMasked;

    private List<DtoWithCollectionsRegularElements> dtos;
}
