package ru.etu.sport.operations.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.operations.domain.OperationKind;

@Data
@NoArgsConstructor
public class UpdateProductOperationDto {
    Long productId;

    Long operationNumber;

    Long operationTypeId;

    Long workCenterId;

    Long professionId;

    Long qualificationId;

    String name;

    String description = "";

    Long setupTime;

    Long pieceTime;

    OperationKind kind;
}
