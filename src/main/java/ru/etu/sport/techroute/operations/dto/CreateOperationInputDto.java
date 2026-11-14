package ru.etu.sport.techroute.operations.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOperationInputDto(
    @NotNull Long operationId,
    @NotNull Integer quantity,
    @NotNull Integer baseQuantity,
    @NotNull Long measureId
) { 
}
