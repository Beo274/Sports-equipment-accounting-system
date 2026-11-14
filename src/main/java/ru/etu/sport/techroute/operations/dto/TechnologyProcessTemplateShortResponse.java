package ru.etu.sport.techroute.operations.dto;

import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplate;

public record TechnologyProcessTemplateShortResponse(
        Long id,
        String name,
        String description
) {
    public static TechnologyProcessTemplateShortResponse fromEntity(TechnologyProcessTemplate e) {
        return new TechnologyProcessTemplateShortResponse(
                e.getId(),
                e.getName(),
                e.getDescription()
        );
    }
}