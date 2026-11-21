package ru.etu.sport.enumeration.dto;

import ru.etu.sport.model.entity.Enumeration;

public record EnumerationResponse(
    Integer id,
    String name,
    String shortName
) {
    public static EnumerationResponse fromEntity(Enumeration e) {
        return new EnumerationResponse(e.getId(), e.getName(), e.getShortName());
    }
}
