package ru.etu.sport.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VariantParameterDto {
    @NotNull
    @Positive
    private Integer parameterId;

    private Integer intVal;

    private Integer enumValueId;
}
