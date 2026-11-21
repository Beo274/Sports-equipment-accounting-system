package ru.etu.sport.product.dto;

public record IntegerConfigurationParameterResponse(
    Integer id,
    Integer parameterId,
    Integer minVal,
    Integer maxVal
) implements ConfigurationParameterResponse {
}