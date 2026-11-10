package ru.etu.sport.techroute.operations.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateOperationInputDto(
    @NotNull Long operationId,
    @NotNull @DecimalMin(value = "0.000001") BigDecimal quantity,
    @NotNull @DecimalMin(value = "0.000001") BigDecimal baseQuantity,
    @NotNull Long measureId
) { 
}
