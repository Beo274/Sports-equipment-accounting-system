package ru.etu.sport.product.dto;

import ru.etu.sport.model.dto.response.ParameterResponse;

public record IntegerVariantParameterResponse(
    ParameterResponse parameter,
    Integer intVal
) implements VariantParameterResponse {
}