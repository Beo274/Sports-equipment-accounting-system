package ru.etu.sport.operations.dto;

import java.math.BigDecimal;

import ru.etu.sport.measure.dto.MeasureResponse;

public record TechnologyRouteInput(
        Long id,
        BigDecimal quantity,
        BigDecimal baseQuantity,
        MeasureResponse measure,
        TechnologyRouteNode operation
) {}