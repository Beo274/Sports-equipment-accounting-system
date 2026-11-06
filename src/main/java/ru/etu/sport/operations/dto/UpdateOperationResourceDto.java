package ru.etu.sport.operations.dto;

public record UpdateOperationResourceDto(
        Long productOperationId,
        Long resourceId,
        ResourceType resourceType,
        Long quantity,
        Long baseQuantity,
        Long unitId
) {
}