package ru.etu.sport.operations.dto;

import ru.etu.sport.operations.persistence.Profession;

public record ProfessionResponse(Long id, String name, String description) {
    public static ProfessionResponse fromEntity(Profession e) {
        return new ProfessionResponse(e.getId(), e.getName(), e.getDescription());
    }
}