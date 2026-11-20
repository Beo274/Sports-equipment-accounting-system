package ru.etu.sport.product.dto;

import ru.etu.sport.enumeration.dto.EnumerationValueResponse;
import ru.etu.sport.model.dto.response.ParameterResponse;
import ru.etu.sport.model.entity.ProductParameter;

public record VariantParameterResponse(
    ParameterResponse parameter,
    Integer intVal,
    EnumerationValueResponse enumValue
) {
    public static VariantParameterResponse fromProductParameter(ProductParameter e) {
        return new VariantParameterResponse(
            ParameterResponse.fromEntity(e.getParameter()), 
            e.getIntVal(), 
            e.getEnumerationValue() != null ? EnumerationValueResponse.fromEntity(e.getEnumerationValue()) : null
        );
    }
}
