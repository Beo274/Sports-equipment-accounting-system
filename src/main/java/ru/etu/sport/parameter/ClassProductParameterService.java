package ru.etu.sport.parameter;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.etu.sport.category.ClassRepository;
import ru.etu.sport.category.projection.ClassHierarchyProjection;
import ru.etu.sport.enumeration.repository.EnumerationValueRepository;
import ru.etu.sport.model.dto.request.ParamBindingDto;
import ru.etu.sport.model.dto.response.ParamBindingResponseDto;
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
    public Integer createClassParam(ParamBindingDto dto) {
        ClassEntity mainClass = classRepository.findById(dto.getClassId()).orElseThrow(EntityNotFoundException::new);
        Parameter param = parameterRepository.findById(dto.getParamId()).orElseThrow(EntityNotFoundException::new);
        EnumerationValue enumVal = dto.getEnumValueId() != null ? enumValueRepository.findById(dto.getEnumValueId()).orElse(null) : null;

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
            ClassEntity childClass = classRepository.findById(childProj.getId()).orElseThrow(EntityNotFoundException::new);

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

    public List<ParamBindingResponseDto> getAllClassParams() {
        return classParameterRepository.findAll().stream().map(cp -> ParamBindingResponseDto.builder()
                .id(cp.getId())
                .classId(cp.getClassEntity().getId())
                .paramId(cp.getParameter().getId())
                .enumValueId(cp.getEnumerationValue() != null ? cp.getEnumerationValue().getId() : null)
                .maxVal(cp.getMaxVal())
                .minVal(cp.getMinVal())
                .intVal(cp.getIntVal())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void deleteClassParam(Integer id) {
        classParameterRepository.deleteById(id);
    }

    @Transactional
    public void updateClassParam(Integer id, ParamBindingDto dto) {
        ClassParameter cp = classParameterRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (dto.getEnumValueId() != null) {
            cp.setEnumerationValue(enumValueRepository.findById(dto.getEnumValueId()).orElse(null));
        }
        cp.setMaxVal(dto.getMaxVal());
        cp.setMinVal(dto.getMinVal());
        cp.setIntVal(dto.getIntVal());
    }

    @Transactional
    public Integer createProductParam(ParamBindingDto dto) {
        Product product = productRepository.findById(Long.valueOf(dto.getProductId())).orElseThrow(EntityNotFoundException::new);
        Parameter param = parameterRepository.findById(dto.getParamId()).orElseThrow(EntityNotFoundException::new);
        EnumerationValue enumVal = dto.getEnumValueId() != null ? enumValueRepository.findById(dto.getEnumValueId()).orElse(null) : null;

        ProductParameter pp = new ProductParameter();
        pp.setProduct(product);
        pp.setParameter(param);
        pp.setEnumerationValue(enumVal);
        pp.setMaxVal(dto.getMaxVal());
        pp.setMinVal(dto.getMinVal());
        pp.setIntVal(dto.getIntVal());
        return productParameterRepository.save(pp).getId();
    }

    public List<ParamBindingResponseDto> getAllProductParams() {
        return productParameterRepository.findAll().stream().map(pp -> ParamBindingResponseDto.builder()
                .id(pp.getId())
                .productId(pp.getProduct().getId())
                .paramId(pp.getParameter().getId())
                .enumValueId(pp.getEnumerationValue() != null ? pp.getEnumerationValue().getId() : null)
                .maxVal(pp.getMaxVal())
                .minVal(pp.getMinVal())
                .intVal(pp.getIntVal())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductParam(Integer id) {
        productParameterRepository.deleteById(id);
    }

    @Transactional
    public void updateProductParam(Integer id, ParamBindingDto dto) {
        ProductParameter pp = productParameterRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (dto.getEnumValueId() != null) {
            pp.setEnumerationValue(enumValueRepository.findById(dto.getEnumValueId()).orElse(null));
        }
        pp.setMaxVal(dto.getMaxVal());
        pp.setMinVal(dto.getMinVal());
        pp.setIntVal(dto.getIntVal());
    }

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

            List<ParamBindingResponseDto> params = entry.getValue().stream().map(pp -> ParamBindingResponseDto.builder()
                    .id(pp.getId())
                    .productId(pp.getProduct().getId())
                    .paramId(pp.getParameter().getId())
                    .enumValueId(pp.getEnumerationValue() != null ? pp.getEnumerationValue().getId() : null)
                    .maxVal(pp.getMaxVal())
                    .minVal(pp.getMinVal())
                    .intVal(pp.getIntVal())
                    .build()).collect(Collectors.toList());
            productInfo.put("params", params);
            items.add(productInfo);
        }
        return Map.of("items", items);
    }

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
                }).collect(Collectors.toList());

        return Map.of("items", items);
    }
}