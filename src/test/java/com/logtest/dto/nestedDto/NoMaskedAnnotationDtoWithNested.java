package com.logtest.dto.nestedDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoMaskedAnnotationDtoWithNested {

    private String someText;

    private Set<Credentials> credentials;

    private IdDocument idDocument;
}
