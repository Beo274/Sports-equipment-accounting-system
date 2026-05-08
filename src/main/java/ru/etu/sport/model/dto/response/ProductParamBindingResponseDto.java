package ru.etu.sport.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductParamBindingResponseDto {
    private Integer id;

    private Integer productId;

    private Integer paramId;

    private Integer enumValueId;

    private Integer maxVal;

    private Integer minVal;

    private Integer intVal;
}
