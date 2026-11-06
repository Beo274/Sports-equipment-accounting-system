package ru.etu.sport.operations.dto;

import java.util.List;

import ru.etu.sport.operations.persistence.ProductOperation;

public record TechnologyRouteOperationResponse(
        Long id,
        Long operationNumber,
        String name,
        WorkCenterResponse workCenter,
        ProfessionResponse profession,
        QualificationResponse qualification,
        Long setupTime,
        Long pieceTime,
        String description,
        List<OperationResourceResponse> resources
) {
    public static TechnologyRouteOperationResponse fromEntity(
            ProductOperation e,
            List<OperationResourceResponse> resources
    ) {
        return new TechnologyRouteOperationResponse(
                e.getId(),
                e.getOperationNumber(),
                e.getName(),
                WorkCenterResponse.fromEntity(e.getWorkCenter()),
                ProfessionResponse.fromEntity(e.getProfession()),
                QualificationResponse.fromEntity(e.getQualification()),
                e.getSetupTime(),
                e.getPieceTime(),
                e.getDescription(),
                resources
        );
    }
}