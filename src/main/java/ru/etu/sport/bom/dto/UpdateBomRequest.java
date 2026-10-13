package ru.etu.sport.bom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBomRequest {
        @JsonSetter (nulls = Nulls.SKIP)
        @Positive (message = "ID must be positive")
        Long parentProductId;

        @JsonSetter(nulls = Nulls.SKIP)
        @Positive(message = "ID must be positive")
        Long componentId;

        @JsonSetter(nulls = Nulls.SKIP)
        @Positive(message = "ID must be positive")
        Integer measureUnitId;

        @JsonSetter(nulls = Nulls.SKIP)
        @Positive(message = "Quantity must be positive")
        Integer quantity;

        @JsonSetter(nulls = Nulls.SKIP)
        @Positive(message = "For quantity must be positive")
        Integer forQuantity;

        @JsonProperty("isBase")
        @JsonSetter(nulls = Nulls.SKIP)
        Boolean isBase;
}