package ru.etu.sport.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnumerationValueDto {
    @NotNull
    private Integer enumerationId;

    private Integer intValue;
    private String stringValue;
    private String imageValue;
    private Integer measureId;
}
