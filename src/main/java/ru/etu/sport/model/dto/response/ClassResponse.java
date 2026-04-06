package ru.etu.sport.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ClassResponse {
    private Integer id;
    private String name;
    private String shortName;
    private Integer baseClassId;
    private Integer mUnitId;
}