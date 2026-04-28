package ru.etu.sport.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttributeValueDto {
    @NotBlank
    private String name;

    @NotBlank
    private String shortName;

    @NotNull
    private Integer attributeId;

    private Integer intValue;
    private String stringValue;
    private String imageValue;
    private Integer measureId;
}
