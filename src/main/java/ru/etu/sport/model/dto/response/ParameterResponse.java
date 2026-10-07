package ru.etu.sport.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.Measure;

@Data
@Builder
@AllArgsConstructor
public class ParameterResponse {
    private Integer id;
    private String name;
    private String shortName;
    private Measure measure;
}
