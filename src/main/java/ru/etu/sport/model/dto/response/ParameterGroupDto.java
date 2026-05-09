package ru.etu.sport.model.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParameterGroupDto {
    private Integer id;
    private String name;
    private String shortName;
    private List<ClassWithParamDto> classes;
    private List<ProductWithParamDto> products;
    private Integer measureId;
}
