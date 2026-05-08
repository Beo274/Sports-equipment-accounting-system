package ru.etu.sport.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParamBindingResponseDto {
    private Integer id;

    @JsonProperty("param_id")
    private Integer paramId;

    @JsonProperty("class_id")
    private Integer classId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("enum_value_id")
    private Integer enumValueId;

    @JsonProperty("max_val")
    private Integer maxVal;

    @JsonProperty("min_val")
    private Integer minVal;

    @JsonProperty("int_val")
    private Integer intVal;
}