package ru.etu.sport.techroute.operations.dto;

import java.math.BigDecimal;

import ru.etu.sport.measure.dto.MeasureResponse;

public record TechnologyProcessTemplateInputResponse(
        Long id,
        Long outputTemplateOperationId,
        BigDecimal quantity,
        BigDecimal baseQuantity,
        MeasureResponse measure,
        TechnologyProcessTemplateOperationResponse operation
) {}