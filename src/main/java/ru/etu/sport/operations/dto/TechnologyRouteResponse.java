package ru.etu.sport.operations.dto;

import java.util.List;

import ru.etu.sport.model.dto.response.ProductResponse;

public record TechnologyRouteResponse(
    ProductResponse product,
    List<TechnologyRouteNode> operations
) {
}