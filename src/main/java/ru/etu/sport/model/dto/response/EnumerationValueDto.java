package ru.etu.sport.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumerationValueDto {
    private Integer id;
    private Integer enumerationId;
    private Integer intValue;
    private String stringValue;
    private String imageValue;
    private Integer position;
    private Integer measureId;
}
