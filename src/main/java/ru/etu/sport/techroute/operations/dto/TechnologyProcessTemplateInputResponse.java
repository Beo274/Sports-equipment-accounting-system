package ru.etu.sport.techroute.operations.dto;

import java.math.BigDecimal;

import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateInput;

public record TechnologyProcessTemplateInputResponse(
        Long id,
        Long outputTemplateOperationId,
        Long inputTemplateOperationId,
        Integer inputOperationNumber,
        String inputOperationName,
        BigDecimal quantity,
        BigDecimal baseQuantity,
        MeasureResponse measure
) {
    public static TechnologyProcessTemplateInputResponse fromEntity(TechnologyProcessTemplateInput e) {
        Long inputOpId = e.getInputTemplateOperation() != null
                ? e.getInputTemplateOperation().getId()
                : (e.getExternalInputOperation() != null ? e.getExternalInputOperation().getId() : null);

        Integer inputOpNum = e.getInputTemplateOperation() != null
                ? e.getInputTemplateOperation().getOperationNumber()
                : (e.getExternalInputOperation() != null && e.getExternalInputOperation().getOperationNumber() != null
                ? e.getExternalInputOperation().getOperationNumber().intValue()
                : null);

        String inputOpName = e.getInputTemplateOperation() != null
                ? e.getInputTemplateOperation().getName()
                : (e.getExternalInputOperation() != null ? e.getExternalInputOperation().getName() : null);

        return new TechnologyProcessTemplateInputResponse(
                e.getId(),
                e.getOutputTemplateOperation().getId(),
                inputOpId,
                inputOpNum,
                inputOpName,
                e.getQuantity(),
                e.getBaseQuantity(),
                e.getMeasure() != null ? MeasureResponse.from(e.getMeasure()) : null
        );
    }
}