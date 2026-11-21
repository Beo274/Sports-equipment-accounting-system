package ru.etu.sport.product.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;

@Schema(
    oneOf = {
        IntegerVariantParameterDto.class,
        EnumVariantParameterDto.class
    },
    discriminatorProperty = "type",
    discriminatorMapping = {
        @DiscriminatorMapping(
            value = "INTEGER",
            schema = IntegerVariantParameterDto.class
        ),
        @DiscriminatorMapping(
            value = "ENUM",
            schema = EnumVariantParameterDto.class
        )
    }
)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = IntegerVariantParameterDto.class,
        name = "INTEGER"
    ),
    @JsonSubTypes.Type(
        value = EnumVariantParameterDto.class,
        name = "ENUM"
    )
})
public sealed interface VariantParameterDto
    permits IntegerVariantParameterDto,
            EnumVariantParameterDto {

    Integer getParameterId();
}