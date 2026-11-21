package ru.etu.sport.product.application;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.category.ClassRepository;
import ru.etu.sport.enumeration.dto.EnumerationValueResponse;
import ru.etu.sport.enumeration.repository.EnumerationValueRepository;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.EnumerationValue;
import ru.etu.sport.model.entity.Parameter;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.model.entity.ProductParameter;
import ru.etu.sport.parameter.ParameterRepository;
import ru.etu.sport.parameter.ProductParameterRepository;
import ru.etu.sport.product.ProductRepository;
import ru.etu.sport.product.dto.ConfigurationParameterDto;
import ru.etu.sport.product.dto.ConfigurationParameterResponse;
import ru.etu.sport.product.dto.ConfigurationResponse;
import ru.etu.sport.product.dto.CreateConfigurableDto;
import ru.etu.sport.product.dto.CreateVariantDto;
import ru.etu.sport.product.dto.EnumConfigurationParameterDto;
import ru.etu.sport.product.dto.EnumConfigurationParameterResponse;
import ru.etu.sport.product.dto.EnumVariantParameterDto;
import ru.etu.sport.product.dto.IntegerConfigurationParameterDto;
import ru.etu.sport.product.dto.IntegerConfigurationParameterResponse;
import ru.etu.sport.product.dto.IntegerVariantParameterDto;
import ru.etu.sport.product.dto.VariantParameterDto;
import ru.etu.sport.product.dto.VariantParameterResponse;
import ru.etu.sport.product.dto.VariantResponse;
import ru.etu.sport.product.model.ProductType;

@Service
@RequiredArgsConstructor
public class ProductConfigurationServiceImpl implements ProductConfigurationService {

    private final ProductRepository productRepository;
    private final ProductParameterRepository productParameterRepository;
    private final ClassRepository classRepository;
    private final EnumerationValueRepository enumerationValueRepository;
    private final ParameterRepository parameterRepository;

    @Override
    public ProductResponse createConfigurable(CreateConfigurableDto dto) {
        Product configuration = new Product();
        
        ClassEntity baseClass = classRepository.findById(dto.getClassId())
            .orElseThrow(() -> new EntityNotFoundException("Base class not found: " + dto.getClassId()));
        
        Product baseProduct = productRepository.findById(dto.getBaseProductId())
            .orElseThrow(() -> new EntityNotFoundException("Base product not found: "+ dto.getBaseProductId()));

        // Мы можем создать типовое изделие только для простого изделия
        if (baseProduct.getType() != ProductType.SIMPLE) {
            throw new ValidationException("Base product for configuration must be SIMPLE");
        }
        
        configuration.setName(dto.getName());
        configuration.setShortName(dto.getShortName());
        configuration.setProductClass(baseClass);
        configuration.setType(ProductType.CONFIGURABLE);
        configuration.setBaseProduct(baseProduct);


        return ProductResponse.from(productRepository.save(configuration));
    }

    @Override
    @Transactional
    public VariantResponse createVariant(CreateVariantDto dto) {
        // Как это происходит:
        // 1. мы берём базовый продукт типового изделия
        // 2. создаём новое VARIANT изделие с именем: "<имя базового>: dto.variantLabel"
        // 3. по всем параметрам типового изделия проверяем входимость переданных значений в ограничения
        // 4. Новому изделию делаем параметры с конкретными значениями
        Product configurable = productRepository.findByIdAndType(dto.getConfigurableId(), ProductType.CONFIGURABLE)
            .orElseThrow(() -> new EntityNotFoundException("Configurable not found: " + dto.getConfigurableId()));

        Product baseProduct = productRepository.findById(configurable.getBaseProduct().getId().longValue())
            .orElseThrow(() -> new EntityNotFoundException("Base product to make variant not found: " + configurable.getBaseProduct().getId()));

        Product variant = new Product();
        variant.setProductClass(baseProduct.getProductClass());
        variant.setBaseProduct(baseProduct.getBaseProduct());
        variant.setType(ProductType.VARIANT);
        variant.setName(baseProduct.getName() + ": " + dto.getVariantLabel());
        variant.setShortName(baseProduct.getShortName() + ": "+ dto.getVariantLabel());

        Map<Parameter, ProductParameter> parameters = productParameterRepository.findByProductId(configurable.getId())
            .stream()
            .filter(param -> param.getIsConfiguration() == true)
            .collect(Collectors.toMap(ProductParameter::getParameter, Function.identity()));
        List<ProductParameter> created = new LinkedList<>();

        variant = productRepository.save(variant);

        for (VariantParameterDto variantParamDto : dto.getParameters()) {
            Parameter param = parameterRepository.findById(variantParamDto.getParameterId())
                .orElseThrow(() -> new EntityNotFoundException("Variant parameter not found: " + variantParamDto.getParameterId()));

            ProductParameter confParam = parameters.get(param);

            ProductParameter variantParam = new ProductParameter();
            variantParam.setParameter(param);
            variantParam.setProduct(variant);

            if (variantParamDto instanceof IntegerVariantParameterDto integerDto) {
                if (!confParam.validateBounds(integerDto.getIntVal())) {
                    throw new ValidationException("Int value out of bounds: " + integerDto.getIntVal());
                }
                variantParam.setIntVal(integerDto.getIntVal());
            } else if (variantParamDto instanceof EnumVariantParameterDto enumDto) {
                EnumerationValue enumValue = enumerationValueRepository.findById(enumDto.getEnumValueId())
                    .orElseThrow(() -> new EntityNotFoundException("Enumeration value not found: " + enumDto.getEnumValueId()));
                
                if (!confParam.validateEnum(enumValue)) {
                    throw new ValidationException("Enum value not allowed: "+enumValue.getId());
                }

                variantParam.setEnumerationValue(enumValue);
            } else {
                throw new IllegalStateException("Unsupported parameter type: " + variantParamDto.getClass().getName());
            }
            
            created.add(variantParam);
        }
        created.forEach(param -> productParameterRepository.save(param));

        return new VariantResponse(
            ProductResponse.from(variant), 
            created.stream().map(param -> VariantParameterResponse.fromEntity(param)).toList()
        );
    }

    @Override
    public void delete(Integer id) {
        Product configurable = productRepository.findByIdAndType(id, ProductType.CONFIGURABLE)
            .orElseThrow(() -> new EntityNotFoundException("Configurable not found: " + id));

        productRepository.delete(configurable);
    }

    @Override
    public void deleteConfigurationParameter(Integer configurationId, Integer parameterId) {
        List<ProductParameter> params = productParameterRepository.findAllByProductIdAndParameterId(configurationId, parameterId);

        params.forEach(param -> productParameterRepository.delete(param));
    }

    @Override
    @Transactional
    public ConfigurationResponse extendConfiguration(Integer id, ConfigurationParameterDto dto) {
        Product configurable = productRepository.findByIdAndType(id, ProductType.CONFIGURABLE)
            .orElseThrow(() -> new EntityNotFoundException("Configurable not found: " + id));

        Parameter parameter = parameterRepository.findById(dto.getParameterId())
            .orElseThrow(() -> new EntityNotFoundException("Parameter not found: " + dto.getParameterId()));

        System.out.println(dto.getClass().getName());
        System.out.println(dto);
        // Нужно обеспечить однозначность конфигурации по одному параметру:
        // мы не будем создавать параметр конфигурации, если у типового изделия
        // уже есть запись с переданным parameterId, но перезапишем уже имеющуюся новыми ограничениями

        List<ProductParameter> parameters = productParameterRepository.findByProductId(id)
            .stream()
            .filter(param -> param.getIsConfiguration() == true)
            .toList();

        Boolean exists = false;

        for (ProductParameter param : parameters) {
            if (param.getParameter().getId() == parameter.getId()) {
                if (dto instanceof IntegerConfigurationParameterDto integerDto) {
                    param.setMaxVal(integerDto.getMaxVal());
                    param.setMinVal(integerDto.getMinVal());
                } else if (dto instanceof EnumConfigurationParameterDto enumDto) {
                    List<EnumerationValue> enumValues = enumerationValueRepository.findAllById(enumDto.getAllowedEnumValuesIds()); 
                    param.setAllowedEnumValues(new HashSet<>(enumValues));
                } else {
                    throw new IllegalStateException("Unsupported config param type: " + dto.getClass().getName());
                }

                productParameterRepository.save(param);
                exists = true;
                break;
            }
        }

        if (!exists) {
            ProductParameter param = new ProductParameter();
             if (dto instanceof IntegerConfigurationParameterDto integerDto) {
                param.setMaxVal(integerDto.getMaxVal());
                param.setMinVal(integerDto.getMinVal());
            } else if (dto instanceof EnumConfigurationParameterDto enumDto) {
                List<EnumerationValue> enumValues = enumerationValueRepository.findAllById(enumDto.getAllowedEnumValuesIds()); 
                if (enumValues.size() == 0) {
                    throw new ValidationException("Unexists enum values: " + enumDto.getAllowedEnumValuesIds());
                }
                param.setAllowedEnumValues(new HashSet<>(enumValues));
            } else {
                throw new IllegalStateException("Unsupported config param type: " + dto.getClass().getName());
            }
            param.setProduct(configurable);
            param.setParameter(parameter);
            param.setIsConfiguration(true);

            productParameterRepository.save(param);
        }

        return buildConfigurationResponse(configurable);
    }

    @Override
    public List<ConfigurationResponse> getAll() {
        List<Product> configurables = productRepository.findAllByType(ProductType.CONFIGURABLE);

        return configurables.stream().map(configurable -> buildConfigurationResponse(configurable)).toList();
    }

    @Override
    public ConfigurationResponse getById(Integer id) {
        Product configurable = productRepository.findByIdAndType(id, ProductType.CONFIGURABLE)
           .orElseThrow(() -> new EntityNotFoundException("Configurable not found: " + id));
        
        return buildConfigurationResponse(configurable);
    }
    

    private ConfigurationResponse buildConfigurationResponse(Product configurable) {
        List<ProductParameter> parameters = productParameterRepository.findByProductId(configurable.getId())
            .stream()
            .filter(param -> param.getIsConfiguration() == true)
            .toList();

        List<ConfigurationParameterResponse> result = new LinkedList<>();

        for (ProductParameter param : parameters) {
            if (param.getIsConfiguration()) {
                Set<EnumerationValue> allowedEnums = param.getAllowedEnumValues();

                if (allowedEnums.size() != 0) {
                    result.add(new EnumConfigurationParameterResponse(
                        param.getId(),
                        param.getParameter().getId(),
                        allowedEnums.stream().map(e -> EnumerationValueResponse.fromEntity(e)).collect(Collectors.toSet())
                    ));
                } else if (param.getMaxVal() != null && param.getMinVal() != null) {
                    result.add(new IntegerConfigurationParameterResponse(
                        param.getId(),
                        param.getParameter().getId(),
                        param.getMinVal(),
                        param.getMaxVal()
                    ));
                } else {
                    throw new IllegalStateException("Parameter has neither enums nor int bounds: " + param.getId());
                }
            }
        }

        return new ConfigurationResponse(ProductResponse.from(configurable), result);
    }
}
