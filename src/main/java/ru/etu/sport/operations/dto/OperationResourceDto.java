package ru.etu.sport.operations.dto;

public record OperationResourceDto(
        Long productOperationId,
        Long resourceId,
        ResourceType resourceType,
        Long quantity,
        Long baseQuantity,
        Long unitId
) {
}