package ru.etu.sport.product.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateVariantDto {
    @NotNull
    @NotBlank
    private String variantLabel;

    @NotNull
    @Positive
    private Integer configurableId;

    @NotEmpty
    List<VariantParameterDto> parameters;
}
