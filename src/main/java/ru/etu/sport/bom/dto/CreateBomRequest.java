package ru.etu.sport.bom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateBomRequest {
    @NotNull(message = "Parent product id required")
    @Positive (message = "ID must be positives")
    Long parentProductId;
    
    @NotNull (message = "Component id required")
    @Positive (message = "ID must be positives")
    Long componentId;

    @NotNull (message = "Measure unit id required")
    @Positive (message = "ID must be positives")
    Integer measureUnitId;

    @NotNull (message = "Quantity required")
    @Positive (message = "Quantity must be positive")
    Integer quantity;

    @NotNull (message = "For quantity value required")
    @Positive (message = "FoQuantity must be positive")
    Integer forQuantity;

    @JsonProperty("isBase")
    Boolean isBase = true;
}
