package ru.etu.sport.product.application;

import java.util.List;

import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.product.dto.ConfigurationParameterDto;
import ru.etu.sport.product.dto.ConfigurationResponse;
import ru.etu.sport.product.dto.CreateConfigurableDto;
import ru.etu.sport.product.dto.CreateVariantDto;
import ru.etu.sport.product.dto.VariantResponse;

public interface ProductConfigurationService {
    ProductResponse createConfigurable(CreateConfigurableDto dto);

    ConfigurationResponse extendConfiguration(Integer id, ConfigurationParameterDto dto);

    VariantResponse createVariant(CreateVariantDto dto);

    List<ConfigurationResponse> getAll();

    ConfigurationResponse getById(Integer id);

    void delete(Integer id);

    void deleteConfigurationParameter(Integer configurationId, Integer parameterId);
}
