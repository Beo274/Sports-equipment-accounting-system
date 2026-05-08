package ru.etu.sport.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ParamBindingDto {
    @JsonProperty("class_id")
    private Integer classId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("param_id")
    private Integer paramId;

    @JsonProperty("enum_value_id")
    private Integer enumValueId;

    @JsonProperty("max_val")
    private Integer maxVal;

    @JsonProperty("min_val")
    private Integer minVal;

    @JsonProperty("int_val")
    private Integer intVal;
}