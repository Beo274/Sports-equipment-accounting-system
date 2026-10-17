package ru.etu.sport.bom.dto;

import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.model.dto.response.ProductResponse;

public record BomItemDto(
        ProductResponse component,
        Double quantity,
        MeasureResponse measureUnit
) {}