package ru.etu.sport.product.dto;

import java.util.Set;

import ru.etu.sport.enumeration.dto.EnumerationValueResponse;

public record EnumConfigurationParameterResponse(
    Integer id,
    Integer parameterId,
    Set<EnumerationValueResponse> enumValues
) implements ConfigurationParameterResponse {
}