package ru.etu.sport.bom.dto;

import java.util.List;
import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.model.dto.response.ProductResponse;

public record BomTreeDto(
        ProductResponse component,
        Double quantity,
        MeasureResponse measureUnit,
        List<BomTreeDto> children
) {}