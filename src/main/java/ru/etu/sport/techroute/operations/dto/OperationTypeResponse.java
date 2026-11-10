package ru.etu.sport.techroute.operations.dto;

import ru.etu.sport.techroute.operations.persistence.OperationType;

public record OperationTypeResponse(Long id, String name, String description) {
    public static OperationTypeResponse fromEntity(OperationType e) {
        return new OperationTypeResponse(e.getId(), e.getName(), e.getDescription());
    }
}
