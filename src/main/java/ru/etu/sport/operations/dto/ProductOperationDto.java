package ru.etu.sport.operations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductOperationDto {
    @NotNull(message = "Product id required")
    @Positive(message = "ID must be positive")
    Long productId;

    Long operationNumber = 1L;

    @NotNull(message = "Operation type id required")
    @Positive(message = "ID must be positive")
    Long operationTypeId;

    @NotNull(message = "Work center id required")
    @Positive(message = "ID must be positive")
    Long workCenterId;

    @NotNull(message = "Profession id required")
    @Positive(message = "ID must be positive")
    Long professionId;

    @NotNull(message = "Qualification id required")
    @Positive(message = "ID must be positive")
    Long qualificationId;

    @NotNull(message = "Name required")
    @NotBlank(message = "Name must not be empty")
    String name;

    String description = "";

    @NotNull(message = "Setup time required")
    @Positive(message = "Time must be positive")
    Long setupTime;

    @NotNull(message = "Piece time required")
    @Positive(message = "Time must be positive")
    Long pieceTime;
}
