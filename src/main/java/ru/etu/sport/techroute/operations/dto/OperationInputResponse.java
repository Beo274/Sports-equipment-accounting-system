package ru.etu.sport.techroute.operations.dto;

import java.math.BigDecimal;

import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.techroute.operations.persistence.OperationInput;

public record OperationInputResponse(
    Long id,
    Long outputOperationId,
    Long inputOperationId,
    ProductResponse inputProduct,
    Long inputOperationNumber,
    BigDecimal quantity,
    BigDecimal baseQuantity,
    MeasureResponse measure
) {
    public static OperationInputResponse fromEntity(OperationInput e) {
        return new OperationInputResponse(
            e.getId(), 
            e.getOutputOperation().getId(), 
            e.getInputOperation().getId(), 
            ProductResponse.from(e.getInputOperation().getProduct()), 
            e.getInputOperation().getOperationNumber(), 
            e.getQuantity(), 
            e.getBaseQuantity(), 
            MeasureResponse.from(e.getMeasure())
        );
    }
}
