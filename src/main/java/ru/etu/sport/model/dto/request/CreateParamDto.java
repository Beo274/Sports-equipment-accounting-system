package ru.etu.sport.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateParamDto {
    @NotBlank
    private String name;

    @NotBlank
    private String shortName;

    private Integer measureId;
}
