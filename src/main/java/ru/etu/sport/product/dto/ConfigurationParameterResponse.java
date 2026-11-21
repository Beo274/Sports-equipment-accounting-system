package ru.etu.sport.product.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = IntegerConfigurationParameterResponse.class,
        name = "INTEGER"
    ),
    @JsonSubTypes.Type(
        value = EnumConfigurationParameterResponse.class,
        name = "ENUM"
    )
})
public sealed interface ConfigurationParameterResponse
    permits IntegerConfigurationParameterResponse,
            EnumConfigurationParameterResponse {

    Integer id();

    Integer parameterId();
}