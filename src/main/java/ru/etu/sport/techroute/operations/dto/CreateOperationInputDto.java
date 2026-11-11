package ru.etu.sport.techroute.operations.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateOperationInputDto(
    @NotNull Long operationId,
    @NotNull Integer quantity,
    @NotNull Integer baseQuantity,
    @NotNull Long measureId
) { 
}
