package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassParamBindingResponseDto {
    private Integer id;

    private Integer classId;

    private Integer paramId;

    private Integer enumValueId;

    private Integer maxVal;

    private Integer minVal;

    private Integer intVal;
}
