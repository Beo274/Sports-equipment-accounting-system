package ru.etu.sport.bom.dto;

import ru.etu.sport.bom.persistence.BomEntity;
import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.model.dto.response.ProductResponse;

public record BomResponse(
        Integer id,
        ProductResponse parentProduct,
        ProductResponse component,
        MeasureResponse measureUnit,
        Integer quantity,
        Integer forQuantity,
        Boolean isBase
) {
    public static BomResponse from(BomEntity e) {
        return new BomResponse(
                e.getId(),
                ProductResponse.from(e.getParentProduct()),
                ProductResponse.from(e.getComponent()),
                e.getMeasure() != null ? MeasureResponse.from(e.getMeasure()) : null,
                e.getQuantity(),
                e.getForQuantity(),
                e.getIsBase()
        );
    }
}