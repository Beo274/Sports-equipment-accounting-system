package ru.etu.sport.product.dto;

import java.util.List;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConfigurationParameterDto {
    @NotNull
    @Positive
    Integer parameterId;

    Integer maxVal;

    Integer minVal;

    List<Integer> allowedEnumValuesIds;

    public void validate() throws ValidationException {
        if (
            maxVal == null && minVal == null && 
            (allowedEnumValuesIds == null || allowedEnumValuesIds != null && allowedEnumValuesIds.size() == 0)
        ) {
            throw new ValidationException("Invalid parameter: null values passed");
        }

        if (maxVal != null && minVal != null && maxVal <= minVal) {
            throw new ValidationException("Invalid parameter: maxVal must be greater than minVal");
        }

        if (maxVal != null && minVal == null || maxVal == null && minVal != null) {
            throw new ValidationException("If maxVal or minVal provided, other bound must be provided too");
        }

        if (allowedEnumValuesIds != null && maxVal != null) {
            throw new ValidationException("Provide int bounds or enum values ids");
        }

        if (allowedEnumValuesIds != null && allowedEnumValuesIds.size() == 0) {
            throw new ValidationException("Enum values ids must not be empty");
        }
    }
}
