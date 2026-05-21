package ru.etu.sport.parameter;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.etu.sport.category.ClassRepository;
import ru.etu.sport.category.projection.ClassHierarchyProjection;
import ru.etu.sport.enumeration.repository.EnumerationValueRepository;
import ru.etu.sport.model.dto.request.ClassParamBindingDto;
import ru.etu.sport.model.dto.request.ProductParamBindingDto;
import ru.etu.sport.model.dto.response.*;
import ru.etu.sport.model.entity.*;
import ru.etu.sport.product.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassProductParameterService {

    private final ClassParameterRepository classParameterRepository;
    private final ProductParameterRepository productParameterRepository;
    private final ClassRepository classRepository;
    private final ProductRepository productRepository;
    private final ParameterRepository parameterRepository;
    private final EnumerationValueRepository enumValueRepository;

    @Transactional
    public Integer createClassParam(ClassParamBindingDto dto) {
        ClassEntity mainClass = classRepository.getReferenceById(dto.getClassId());
        Parameter param = parameterRepository.getReferenceById(dto.getParamId());
        EnumerationValue enumVal = dto.getEnumValueId() != null ? enumValueRepository.findById(dto.getEnumValueId())
            .orElseThrow(() -> new EntityNotFoundException("Enumeration not found with id: " + dto.getEnumValueId())) : null;

        ClassParameter cp = new ClassParameter();
        cp.setClassEntity(mainClass);
        cp.setParameter(param);
        cp.setEnumerationValue(enumVal);
        cp.setMaxVal(dto.getMaxVal());
        cp.setMinVal(dto.getMinVal());
        cp.setIntVal(dto.getIntVal());
        cp = classParameterRepository.save(cp);

        List<ClassHierarchyProjection> children = classRepository.findChildren(mainClass.getId());
        List<Integer> allAffectedClassIds = new ArrayList<>();
        allAffectedClassIds.add(mainClass.getId());

        for (ClassHierarchyProjection childProj : children) {
            if (childProj.getId().equals(mainClass.getId())) continue;
            ClassEntity childClass = classRepository.getReferenceById(childProj.getId());

            ClassParameter childCp = new ClassParameter();
            childCp.setClassEntity(childClass);
            childCp.setParameter(param);
            childCp.setEnumerationValue(enumVal);
            childCp.setMaxVal(dto.getMaxVal());
            childCp.setMinVal(dto.getMinVal());
            childCp.setIntVal(dto.getIntVal());
            classParameterRepository.save(childCp);

            allAffectedClassIds.add(childProj.getId());
        }

        List<Product> products = productRepository.findByProductClass_IdIn(allAffectedClassIds);
        for (Product product : products) {
            ProductParameter pp = new ProductParameter();
            pp.setProduct(product);
            pp.setParameter(param);
            pp.setEnumerationValue(enumVal);
            pp.setMaxVal(dto.getMaxVal());
            pp.setMinVal(dto.getMinVal());
            pp.setIntVal(dto.getIntVal());
            productParameterRepository.save(pp);
        }

        return cp.getId();
    }

    public List<ClassParamBindingResponseDto> getAllClassParams() {
        return classParameterRepository.findAll().stream().map(cp -> ClassParamBindingResponseDto.builder()
                .id(cp.getId())
                .classId(cp.getClassEntity().getId())
                .paramId(cp.getParameter().getId())
                .enumValue(cp.getEnumerationValue())
                .maxVal(cp.getMaxVal())
                .minVal(cp.getMinVal())
                .intVal(cp.getIntVal())
                .build()).toList();
    }

    public List<ClassParamBindingResponseDto> getClassParamsById(Integer classId) {
        return classParameterRepository.findByClassId(classId).stream().map(cp -> ClassParamBindingResponseDto.builder()
                .id(cp.getId())
                .classId(cp.getClassEntity().getId())
                .paramId(cp.getParameter().getId())
                .enumValue(cp.getEnumerationValue())
                .maxVal(cp.getMaxVal())
                .minVal(cp.getMinVal())
                .intVal(cp.getIntVal())
                .build()).toList();
    }

    @Transactional
    public void deleteClassParam(Integer id) {
        classParameterRepository.deleteById(id);
    }

    @Transactional
    public void updateClassParam(Integer id, ClassParamBindingDto dto) {
        ClassParameter cp = classParameterRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (dto.getEnumValueId() != null) {
            cp.setEnumerationValue(enumValueRepository.findById(dto.getEnumValueId()).orElse(null));
        }
        cp.setMaxVal(dto.getMaxVal());
        cp.setMinVal(dto.getMinVal());
        cp.setIntVal(dto.getIntVal());
    }

    @Transactional
    public Integer createProductParam(ProductParamBindingDto dto) {
        Product product = productRepository.getReferenceById(Long.valueOf(dto.getProductId()));
        Parameter param = parameterRepository.getReferenceById(dto.getParamId());
        EnumerationValue enumVal = dto.getEnumValueId() != null ? enumValueRepository.getReferenceById(dto.getEnumValueId()) : null;

        ProductParameter pp = new ProductParameter();
        pp.setProduct(product);
        pp.setParameter(param);
        pp.setEnumerationValue(enumVal);
        pp.setMaxVal(dto.getMaxVal());
        pp.setMinVal(dto.getMinVal());
        pp.setIntVal(dto.getIntVal());
        return productParameterRepository.save(pp).getId();
    }

    public List<ProductParamBindingResponseDto> getAllProductParams() {
        return productParameterRepository.findAll().stream().map(pp -> ProductParamBindingResponseDto.builder()
                .id(pp.getId())
                .productId(pp.getProduct().getId())
                .paramId(pp.getParameter().getId())
                .enumValue(pp.getEnumerationValue())
                .maxVal(pp.getMaxVal())
                .minVal(pp.getMinVal())
                .intVal(pp.getIntVal())
                .build()).toList();
    }

    public List<ProductParamBindingResponseDto> getProductParams(Integer productId) {
        return productParameterRepository.findByProductId(productId).stream()
            .map(pp -> ProductParamBindingResponseDto.builder()
                .id(pp.getId())
                .productId(pp.getProduct().getId())
                .paramId(pp.getParameter().getId())
                .enumValue(pp.getEnumerationValue())
                .maxVal(pp.getMaxVal())
                .minVal(pp.getMinVal())
                .intVal(pp.getIntVal())
                .build()).toList();
    }

    public void deleteProductParam(Integer id) {
        productParameterRepository.deleteById(id);
    }

    @Transactional
    public void updateProductParam(Integer id, ProductParamBindingDto dto) {
        ProductParameter pp = productParameterRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (dto.getEnumValueId() != null) {
            pp.setEnumerationValue(enumValueRepository.findById(dto.getEnumValueId()).orElse(null));
        }
        pp.setMaxVal(dto.getMaxVal());
        pp.setMinVal(dto.getMinVal());
        pp.setIntVal(dto.getIntVal());
    }

    @Transactional
    public Map<String, Object> getProductsWithParamsByClass(Integer classId) {
        List<ProductParameter> allParams = productParameterRepository.findByProduct_ProductClass_Id(classId);

        List<Map<String, Object>> items = new ArrayList<>();
        Map<Integer, List<ProductParameter>> groupedByProduct = allParams.stream()
                .collect(Collectors.groupingBy(pp -> pp.getProduct().getId()));

        for (Map.Entry<Integer, List<ProductParameter>> entry : groupedByProduct.entrySet()) {
            Product product = entry.getValue().get(0).getProduct();
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("id", product.getId());
            productInfo.put("name", product.getName());
            productInfo.put("short_name", product.getShortName());
            productInfo.put("class_id", product.getProductClass().getId());

            List<ProductParamBindingResponseDto> params = entry.getValue().stream().map(pp -> ProductParamBindingResponseDto.builder()
                    .id(pp.getId())
                    .productId(pp.getProduct().getId())
                    .paramId(pp.getParameter().getId())
                    .enumValue(pp.getEnumerationValue())
                    .maxVal(pp.getMaxVal())
                    .minVal(pp.getMinVal())
                    .intVal(pp.getIntVal())
                    .build()).toList();
            productInfo.put("params", params);
            items.add(productInfo);
        }
        return Map.of("items", items);
    }

    @Transactional
    public Map<String, Object> getProductsByParamValue(Integer productParamId) {
        ProductParameter sourceParam = productParameterRepository.findById(productParamId).orElseThrow(EntityNotFoundException::new);
        Integer enumId = sourceParam.getEnumerationValue() != null ? sourceParam.getEnumerationValue().getId() : null;

        List<ProductParameter> matchingParams = productParameterRepository.findByParameter_IdAndIntValAndEnumerationValue_Id(
                sourceParam.getParameter().getId(), sourceParam.getIntVal(), enumId);

        List<Map<String, Object>> items = matchingParams.stream()
                .map(ProductParameter::getProduct)
                .distinct()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("short_name", p.getShortName());
                    map.put("class_id", p.getProductClass().getId());
                    return map;
                }).toList();

        return Map.of("items", items);
    }

    @Transactional
    public List<ParameterGroupDto> getParamsGroups() {
        List<Parameter> parameters = this.parameterRepository.findAllWithMeasure();

        if (parameters.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> paramIds = parameters.stream()
            .map(Parameter::getId)
            .collect(Collectors.toList());

        List<ClassParameter> classParameters = this.classParameterRepository.findByParamIds(paramIds);
        List<ProductParameter> productParameters = this.productParameterRepository.findByParamIds(paramIds);

        Map<Integer, List<ClassParameter>> classParamsByParamId = classParameters.stream()
            .collect(Collectors.groupingBy(cp -> cp.getParameter().getId()));
        Map<Integer, List<ProductParameter>> productParamsByParamId = productParameters.stream()
            .collect(Collectors.groupingBy(pp -> pp.getParameter().getId()));

        return parameters.stream()
            .map(param -> ParameterGroupDto.buildParamGroup(
                param, 
                classParamsByParamId.getOrDefault(param.getId(), Collections.emptyList()), 
                productParamsByParamId.getOrDefault(param.getId(), Collections.emptyList())
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public void inheritParametersForClass(Integer classId) {
        List<ClassHierarchyProjection> parents = this.classRepository.findParents(classId);
        if (parents.isEmpty()) {
            return;
        }

        ClassEntity currentClass = this.classRepository.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + classId));

        List<ClassParameter> existingParameters = classParameterRepository.findByClassIdWithParam(classId);
        Set<Integer> existingParamsIds = existingParameters.stream()
            .map(cp -> cp.getParameter().getId())
            .collect(Collectors.toSet());
        
        List<ClassParameter> newParameters = new ArrayList<>();

        for (ClassHierarchyProjection parent : parents) {
            if (parent.getId() == currentClass.getId()) {
                continue;
            }

            List<ClassParameter> parentParameters = classParameterRepository.findByClassIdWithParam(parent.getId());
            
            for (ClassParameter parentParam: parentParameters) {
                if (!existingParamsIds.contains(parentParam.getId())) {
                    ClassParameter newParam = ClassParameter.builder()
                        .classEntity(currentClass)
                        .parameter(parentParam.getParameter())
                        .enumerationValue(parentParam.getEnumerationValue())
                        .maxVal(parentParam.getMaxVal())
                        .minVal(parentParam.getMinVal())
                        .intVal(parentParam.getIntVal())
                        .build();
                    
                    newParameters.add(newParam);
                    existingParamsIds.add(parentParam.getId());
                }
            }
        }

        if (!newParameters.isEmpty()) {
            classParameterRepository.saveAll(newParameters);
        }
    }

    @Transactional
    public void inheritParametersForProduct(Integer productId) {
        Product product = this.productRepository.findById(productId.longValue())
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        if (product.getProductClass() == null) {
            return;
        }

        Integer classId = product.getProductClass().getId();
        List<ClassHierarchyProjection> parents = this.classRepository.findParents(classId);
        if (parents.isEmpty()) {
            return;
        }

        List<ClassParameter> existingParameters = classParameterRepository.findByClassIdWithParam(classId);
        Set<Integer> existingParamsIds = existingParameters.stream()
            .map(cp -> cp.getParameter().getId())
            .collect(Collectors.toSet());
        List<ProductParameter> newParameters = new ArrayList<>();

        for (ClassHierarchyProjection parent : parents) {
            List<ClassParameter> parentParameters = classParameterRepository.findByClassIdWithParam(parent.getId());
            
            for (ClassParameter parentParam: parentParameters) {
                if (!existingParamsIds.contains(parentParam.getId())) {
                    ProductParameter newParam = ProductParameter.builder()
                        .product(product)
                        .parameter(parentParam.getParameter())
                        .enumerationValue(parentParam.getEnumerationValue())
                        .maxVal(parentParam.getMaxVal())
                        .minVal(parentParam.getMinVal())
                        .intVal(parentParam.getIntVal())
                        .build();
                    
                    newParameters.add(newParam);
                    existingParamsIds.add(parentParam.getId());
                }
            }
        }

        if (!newParameters.isEmpty()) {
            this.productParameterRepository.saveAll(newParameters);
        }
    }

    @Transactional
    public List<ProductWithParamDto> getProductsInRange(Integer paramId, Double minVal, Double maxVal) {
        Integer min = minVal != null ? minVal.intValue() : Integer.MIN_VALUE;
        Integer max = maxVal != null ? maxVal.intValue() : Integer.MAX_VALUE;

        List<Integer> targetProductIds = productParameterRepository.findProductIdsByParamRange(paramId, min, max);

        if (targetProductIds.isEmpty()) {
            return List.of();
        }

        List<ProductParameter> allTargetParameters = productParameterRepository.findAllParamsForProducts(targetProductIds);

        return ProductParameter.mapProductParameter(allTargetParameters);
    }

    public List<ProductWithParamDto> getProductsBySeveralParams(List<Integer> paramIds) {
        Long paramCount = paramIds.stream().distinct().count();

        List<Integer> targetProductIds = productParameterRepository.findProductIdsByAllParams(paramIds, paramCount);

        if (targetProductIds.isEmpty()) {
            return List.of();
        }

        List<ProductParameter> allProductParameters = productParameterRepository.findAllParamsForProducts(targetProductIds);

        return allProductParameters.stream()
                .collect(Collectors.groupingBy(ProductParameter::getProduct))
                .entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    List<ProductParameter> paramsForProduct = entry.getValue();

                    // Создаем списки для агрегации параметров этого продукта
                    List<EnumerationValueDto> enumValues = new ArrayList<>();
                    List<Integer> maxVals = new ArrayList<>();
                    List<Integer> minVals = new ArrayList<>();
                    List<Integer> intVals = new ArrayList<>();

                    // Заполняем списки значениями (с вашей логикой фильтрации null)
                    for (ProductParameter pp : paramsForProduct) {
                        enumValues.add(EnumerationValue.mapEnumerationValue(pp.getEnumerationValue()));
                        maxVals.add(pp.getEnumerationValue() != null ? null : pp.getMaxVal());
                        minVals.add(pp.getEnumerationValue() != null ? null : pp.getMinVal());
                        intVals.add(pp.getEnumerationValue() != null ? null : pp.getIntVal());
                    }

                    // Собираем итоговое DTO, где продукт представлен в единственном экземпляре
                    return ProductWithParamDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .shortName(product.getShortName())
                            .classId(product.getProductClass() != null ? product.getProductClass().getId() : null)
                            .paramEnumValue(enumValues)
                            .maxVal(maxVals)
                            .minVal(minVals)
                            .intVal(intVals)
                            .build();
                })
                .collect(Collectors.toList());
    }
}