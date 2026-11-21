package ru.etu.sport.product.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "EnumConfigurationParameter")
public final class EnumConfigurationParameterDto
    implements ConfigurationParameterDto {

    @NotNull 
    @Positive 
    private Integer parameterId;

    @NotEmpty
    private List<@NotNull @Positive Integer> allowedEnumValuesIds;
}