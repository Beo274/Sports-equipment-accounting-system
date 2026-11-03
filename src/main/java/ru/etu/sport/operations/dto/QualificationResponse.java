package ru.etu.sport.operations.dto;

import ru.etu.sport.operations.persistence.Qualification;

public record QualificationResponse(Long id, String name, String description) {
    public static QualificationResponse fromEntity(Qualification e) {
        return new QualificationResponse(e.getId(), e.getName(), e.getDescription());
    }
}
