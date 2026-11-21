package ru.etu.sport.product.dto;

import java.util.List;

import ru.etu.sport.model.dto.response.ProductResponse;

public record VariantResponse(
    ProductResponse variant,
    List<VariantParameterResponse> parameters
) {
    
}
