package ru.etu.sport.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductParamBindingDto {
    @NotNull
    private Integer productId;

    @NotNull
    private Integer paramId;

    private Integer enumValueId;

    private Integer maxVal;

    private Integer minVal;

    private Integer intVal;
}
