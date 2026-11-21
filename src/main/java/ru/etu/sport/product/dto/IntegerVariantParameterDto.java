package ru.etu.sport.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "IntegerVariantParameterDto")
public final class IntegerVariantParameterDto
    implements VariantParameterDto {

    @NotNull
    @Positive
    private Integer parameterId;

    @NotNull
    private Integer intVal;
}