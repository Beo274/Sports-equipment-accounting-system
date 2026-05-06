package ru.etu.sport.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateProductDto {
    @NotBlank
    private String name;

    @NotBlank
    private String shortName;

    @NotNull
    private Integer classId;
}
