package ru.etu.sport.techroute.operations.dto;

import java.util.List;

import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplate;

public record TechnologyProcessTemplateResponse(
        Long id,
        String name,
        String description,
        List<TechnologyProcessTemplateOperationResponse> operations
) {
    public static TechnologyProcessTemplateResponse fromEntity(
            TechnologyProcessTemplate e,
            List<TechnologyProcessTemplateOperationResponse> operations
    ) {
        return new TechnologyProcessTemplateResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                operations
        );
    }
}