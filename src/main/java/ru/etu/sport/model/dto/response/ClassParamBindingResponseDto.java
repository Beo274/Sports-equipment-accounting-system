package ru.etu.sport.model.dto.response;


import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.EnumerationValue;

@Data
@Builder
public class ClassParamBindingResponseDto {
    private Integer id;

    private Integer classId;

    private Integer paramId;

    private EnumerationValue enumValue;

    private Integer maxVal;

    private Integer minVal;

    private Integer intVal;
}
