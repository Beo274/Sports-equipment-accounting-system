package ru.etu.sport.product.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ru.etu.sport.enumeration.dto.EnumerationValueResponse;
import ru.etu.sport.model.dto.response.ParameterResponse;
import ru.etu.sport.model.entity.ProductParameter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = IntegerVariantParameterResponse.class,
        name = "INTEGER"
    ),
    @JsonSubTypes.Type(
        value = EnumVariantParameterResponse.class,
        name = "ENUM"
    )
})
public sealed interface VariantParameterResponse
    permits IntegerVariantParameterResponse,
            EnumVariantParameterResponse {

    ParameterResponse parameter();

    static VariantParameterResponse fromEntity(ProductParameter e) {
        ParameterResponse parameter =
            ParameterResponse.fromEntity(e.getParameter());

        if (e.getIntVal() != null) {
            return new IntegerVariantParameterResponse(
                parameter,
                e.getIntVal()
            );
        }

        if (e.getEnumerationValue() != null) {
            return new EnumVariantParameterResponse(
                parameter,
                EnumerationValueResponse.fromEntity(
                    e.getEnumerationValue()
                )
            );
        }

        throw new IllegalStateException(
            "ProductParameter has neither intVal nor enumerationValue"
        );
    }
}
