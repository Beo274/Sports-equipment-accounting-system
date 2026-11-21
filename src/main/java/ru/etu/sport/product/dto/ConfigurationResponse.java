package ru.etu.sport.product.dto;

import java.util.List;

import ru.etu.sport.model.dto.response.ProductResponse;

public record ConfigurationResponse(
    ProductResponse product,
    List<ConfigurationParameterResponse> parameters
) {
}
