package ru.etu.sport.measure.dto;

import ru.etu.sport.model.entity.Measure;

public record MeasureResponse(Integer id, String name, String shortName) {
    public static MeasureResponse from(Measure e) {
        return new MeasureResponse(e.getId(), e.getName(), e.getShortName());
    }
}
