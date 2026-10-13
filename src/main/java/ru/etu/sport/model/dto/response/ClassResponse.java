package ru.etu.sport.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.ClassEntity;

@Data
@Builder
@AllArgsConstructor
public class ClassResponse {
    private Integer id;
    private String name;
    private String shortName;
    private Integer baseClassId;
    private Integer mUnitId;

    public static ClassResponse from(ClassEntity e) {
        return new ClassResponse(
                e.getId(),
                e.getName(),
                e.getShortName(),
                e.getBaseClass() != null ? e.getBaseClass().getId() : null,
                e.getMeasure() != null ? e.getMeasure().getId() : null
        );
    }
}