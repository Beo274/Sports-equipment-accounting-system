package ru.etu.sport.product.dto;

import java.util.Set;

import ru.etu.sport.enumeration.dto.EnumerationValueResponse;

public record ConfigurationParameterResponse(
    Integer id,
    Integer parameterId,
    Integer maxVal,
    Integer minVal,
    Set<EnumerationValueResponse> allowedEnums 
) {

}
