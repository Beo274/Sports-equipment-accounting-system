package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.EnumerationValue;

@Data
@Builder
public class ProductParamBindingResponseDto {
    private Integer id;

    private Integer productId;

    private Integer paramId;

    private EnumerationValue enumValue;

    private Integer maxVal;

    private Integer minVal;

    private Integer intVal;
}
