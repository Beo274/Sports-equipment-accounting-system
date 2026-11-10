package ru.etu.sport.techroute.operations.dto;

import ru.etu.sport.techroute.operations.domain.ResourceType;

public record UpdateOperationResourceDto(
        Long productOperationId,
        Long resourceId,
        ResourceType resourceType,
        Long quantity,
        Long baseQuantity,
        Long unitId
) {
}