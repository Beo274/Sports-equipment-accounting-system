package ru.etu.sport.operations.dto;

import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.operations.persistence.OperationResource;

public record OperationResourceResponse(
        Long id,
        Long productOperationId,
        ProductResponse resource,
        ResourceType resourceType,
        Long quantity,
        Long baseQuantity,
        MeasureResponse unit
) {
    public static OperationResourceResponse fromEntity(OperationResource e) {
        return new OperationResourceResponse(
                e.getId(),
                e.getProductOperation().getId(),
                ProductResponse.from(e.getResource()),
                e.getResourceType(),
                e.getQuantity(),
                e.getBaseQuantity(),
                MeasureResponse.from(e.getUnit())
        );
    }
}