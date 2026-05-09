package ru.etu.sport.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassWithParamDto {
    private Integer id;
    private String name;
    private String shortName;
    private Integer baseClassId;
    private EnumerationValueDto paramEnumValue;
    private Integer maxVal;
    private Integer minVal;
    private Integer intVal;
}