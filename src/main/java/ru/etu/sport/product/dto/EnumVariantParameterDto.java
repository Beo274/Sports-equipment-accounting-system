package ru.etu.sport.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "EnumVariantParameterDto")
public final class EnumVariantParameterDto
    implements VariantParameterDto {

    @NotNull
    @Positive
    private Integer parameterId;

    @NotNull
    @Positive
    private Integer enumValueId;
}
