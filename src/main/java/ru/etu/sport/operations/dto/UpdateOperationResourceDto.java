package ru.etu.sport.operations.dto;

import ru.etu.sport.operations.domain.ResourceType;

public record UpdateOperationResourceDto(
        Long productOperationId,
        Long resourceId,
        ResourceType resourceType,
        Long quantity,
        Long baseQuantity,
        Long unitId
) {
}