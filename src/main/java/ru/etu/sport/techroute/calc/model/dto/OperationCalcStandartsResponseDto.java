package ru.etu.sport.techroute.calc.model.dto;

import java.util.List;

public record OperationCalcStandartsResponseDto(
        String operationName,
        Integer number,
        List<MaterialResponseDto> materials
) {
//    public static OperationCalcStandartsResponseDto fromEntity(
//
//    )
}


