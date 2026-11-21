package ru.etu.sport.model.dto.response;

import java.util.List;

import ru.etu.sport.model.entity.Product;
import ru.etu.sport.model.entity.ProductParameter;
import ru.etu.sport.parameter.dto.ProductParameterResponse;

public record ProductWithParamsResponse(
    ProductResponse product,
    List<ProductParameterResponse> parameters
) {
    public static ProductWithParamsResponse from(Product p, List<ProductParameter> pp) {
        return new ProductWithParamsResponse(
            ProductResponse.from(p), 
            pp.stream().map(param -> ProductParameterResponse.fromEntity(param)).toList()
        );
    }
}
