package ru.etu.sport.product.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;

@Schema(
    oneOf = {
        IntegerConfigurationParameterDto.class,
        EnumConfigurationParameterDto.class
    },
    discriminatorProperty = "type",
    discriminatorMapping = {
        @DiscriminatorMapping(
            value = "INTEGER",
            schema = IntegerConfigurationParameterDto.class
        ),
        @DiscriminatorMapping(
            value = "ENUM",
            schema = EnumConfigurationParameterDto.class
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
        value = IntegerConfigurationParameterDto.class,
        name = "INTEGER"
    ),
    @JsonSubTypes.Type(
        value = EnumConfigurationParameterDto.class,
        name = "ENUM"
    )
})
public sealed interface ConfigurationParameterDto
    permits IntegerConfigurationParameterDto,
            EnumConfigurationParameterDto {

    Integer getParameterId();
}
