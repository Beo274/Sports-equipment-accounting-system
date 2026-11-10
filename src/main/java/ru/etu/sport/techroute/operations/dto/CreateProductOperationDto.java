package ru.etu.sport.techroute.operations.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.etu.sport.techroute.operations.domain.OperationKind;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateProductOperationDto extends BaseCreateRequestDto {
    @NotNull(message = "Product id required")
    @Positive(message = "ID must be positive")
    Long productId;

    Long operationNumber = 1L;

    Long operationTypeId;

    Long workCenterId;

    Long professionId;

    Long qualificationId;

    @NotNull(message = "Setup time required")
    @Positive(message = "Time must be positive")
    Long setupTime;

    @NotNull(message = "Piece time required")
    @Positive(message = "Time must be positive")
    Long pieceTime;

    @NotNull(message = "Operation kind must be provided")
    OperationKind kind;
}
