package ru.etu.sport.techroute.operations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseCreateRequestDto {
    @NotNull(message = "Name required")
    @NotBlank(message = "Name must not be empty")
    String name;

    String description = "";
}
