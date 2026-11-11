package ru.etu.sport.techroute.operations.dto;

import ru.etu.sport.techroute.operations.persistence.WorkCenter;

public record WorkCenterResponse(Long id, String name, String description) {
    public static WorkCenterResponse fromEntity(WorkCenter e) {
        return new WorkCenterResponse(e.getId(), e.getName(), e.getDescription());
    }
}
