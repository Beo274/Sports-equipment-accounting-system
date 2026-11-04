package ru.etu.sport.operations.dto;

import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.operations.persistence.ProductOperation;

public record ProductOperationReponse(
        Long id,
        ProductResponse product,
        Long operationNumber,
        String name,
        WorkCenterResponse workCenter,
        ProfessionResponse profession,
        QualificationResponse qualification,
        Long setupTime,
        Long pieceTime,
        String description) {
    public static ProductOperationReponse fromEntity(ProductOperation e) {
        return new ProductOperationReponse(
                e.getId(),
                ProductResponse.from(e.getProduct()),
                e.getOperationNumber(),
                e.getName(),
                WorkCenterResponse.fromEntity(e.getWorkCenter()),
                ProfessionResponse.fromEntity(e.getProfession()),
                QualificationResponse.fromEntity(e.getQualification()),
                e.getSetupTime(),
                e.getPieceTime(),
                e.getDescription());
    }
}
