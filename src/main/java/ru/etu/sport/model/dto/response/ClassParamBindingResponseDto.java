package ru.etu.sport.model.dto.response;


import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.EnumerationValue;
import ru.etu.sport.model.entity.Parameter;

@Data
@Builder
public class ClassParamBindingResponseDto {
    private Integer id;

    private Integer classId;

    private Parameter param;

    private EnumerationValue enumValue;

    private Integer maxVal;

    private Integer minVal;

    private Integer intVal;
}
