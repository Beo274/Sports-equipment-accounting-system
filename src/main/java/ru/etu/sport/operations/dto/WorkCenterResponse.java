package ru.etu.sport.operations.dto;

import ru.etu.sport.operations.persistence.WorkCenter;

public record WorkCenterResponse(Long id, String name, String description) {
    public static WorkCenterResponse fromEntity(WorkCenter e) {
        return new WorkCenterResponse(e.getId(), e.getName(), e.getDescription());
    }
}
