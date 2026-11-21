package ru.etu.sport.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "IntegerConfigurationParameter")
public final class IntegerConfigurationParameterDto
    implements ConfigurationParameterDto {

    @NotNull
    @Positive
    private Integer parameterId;

    @NotNull
    private Integer minVal;

    @NotNull
    private Integer maxVal;

    @AssertTrue(message = "maxVal must be greater than minVal")
    public boolean isValidBounds() {
        return minVal == null || maxVal == null || maxVal > minVal;
    }
}
