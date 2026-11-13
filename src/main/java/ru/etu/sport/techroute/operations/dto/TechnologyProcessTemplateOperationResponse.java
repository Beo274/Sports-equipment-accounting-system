package ru.etu.sport.techroute.operations.dto;

import java.util.List;

import ru.etu.sport.techroute.operations.domain.OperationKind;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateOperation;

public record TechnologyProcessTemplateOperationResponse(
        Long id,
        Integer operationNumber,
        String name,
        String description,
        OperationKind kind,
        Long setupTime,
        Long pieceTime,
        WorkCenterResponse workCenter,
        ProfessionResponse profession,
        QualificationResponse qualification,
        List<TechnologyProcessTemplateInputResponse> inputs
) {
    public static TechnologyProcessTemplateOperationResponse fromEntity(
            TechnologyProcessTemplateOperation e,
            List<TechnologyProcessTemplateInputResponse> inputs
    ) {
        return new TechnologyProcessTemplateOperationResponse(
                e.getId(),
                e.getOperationNumber(),
                e.getName(),
                e.getDescription(),
                e.getKind(),
                e.getSetupTime(),
                e.getPieceTime(),
                e.getWorkCenter() != null ? WorkCenterResponse.fromEntity(e.getWorkCenter()) : null,
                e.getProfession() != null ? ProfessionResponse.fromEntity(e.getProfession()) : null,
                e.getQualification() != null ? QualificationResponse.fromEntity(e.getQualification()) : null,
                inputs
        );
    }
}