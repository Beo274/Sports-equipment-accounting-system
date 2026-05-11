package ru.etu.sport.model.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import ru.etu.sport.model.entity.ClassParameter;
import ru.etu.sport.model.entity.Parameter;
import ru.etu.sport.model.entity.ProductParameter;

@Data
@Builder
public class ParameterGroupDto {
    private Integer id;
    private String name;
    private String shortName;
    private List<ClassWithParamDto> classes;
    private List<ProductWithParamDto> products;
    private Integer measureId;

    public static ParameterGroupDto buildParamGroup(
        Parameter parameter, 
        List<ClassParameter> classParameters, 
        List<ProductParameter> productParameters
    ) {
        return ParameterGroupDto.builder()
            .id(parameter.getId())
            .name(parameter.getName())
            .shortName(parameter.getShortName())
            .classes(ClassParameter.mapClassParameter(classParameters))
            .products(ProductParameter.mapProductParameter(productParameters))
            .build();
    }
}
