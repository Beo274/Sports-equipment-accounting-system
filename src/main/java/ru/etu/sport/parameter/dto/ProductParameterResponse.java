package ru.etu.sport.parameter.dto;

import ru.etu.sport.enumeration.dto.EnumerationValueResponse;
import ru.etu.sport.model.dto.response.ParameterResponse;
import ru.etu.sport.model.entity.ProductParameter;

public record ProductParameterResponse(
    Integer id,
    ParameterResponse parameter,
    Integer intVal,
    Integer maxVal,
    Integer minVal,
    EnumerationValueResponse enumValue
) {
    public static ProductParameterResponse fromEntity(ProductParameter pp) {
        return new ProductParameterResponse(
            pp.getId(),
            ParameterResponse.fromEntity(pp.getParameter()), 
            pp.getIntVal(), 
            pp.getMaxVal(), 
            pp.getMinVal(), 
            pp.getEnumerationValue() != null ? EnumerationValueResponse.fromEntity(pp.getEnumerationValue()) : null
        );
    }
}
