package ru.etu.sport.enumeration.dto;

import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.model.entity.EnumerationValue;

public record EnumerationValueResponse(
    Integer id,
    EnumerationResponse enumeration,
    Integer intValue,
    String stringValue,
    MeasureResponse measure
) {
    public static EnumerationValueResponse fromEntity(EnumerationValue e) {
        return new EnumerationValueResponse(
            e.getId(),
            EnumerationResponse.fromEntity(e.getEnumeration()), 
            e.getIntValue(),
            e.getStringValue(),
            e.getMeasure() != null ? MeasureResponse.from(e.getMeasure()) : null
        );
    }
}
