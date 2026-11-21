package ru.etu.sport.product.dto;

import ru.etu.sport.enumeration.dto.EnumerationValueResponse;
import ru.etu.sport.model.dto.response.ParameterResponse;

public record EnumVariantParameterResponse(
    ParameterResponse parameter,
    EnumerationValueResponse enumValue
) implements VariantParameterResponse {
}