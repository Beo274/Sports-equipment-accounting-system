package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductWithParamDto {
    private Integer id;
    private String name;
    private String shortName;
    private Integer classId;
    private EnumerationValueDto paramEnumValue;
    private Integer maxVal;
    private Integer minVal;
    private Integer intVal;
}
