package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductWithParamDto {
    private Integer id;
    private String name;
    private String shortName;
    private Integer classId;

    // Теперь это списки, чтобы хранить все параметры одного продукта вместе
    private List<EnumerationValueDto> paramEnumValue; // Укажите ваш точный тип для Enum DTO
    private List<Integer> maxVal;
    private List<Integer> minVal;
    private List<Integer> intVal;
}
