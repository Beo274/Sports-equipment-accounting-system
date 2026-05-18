package ru.etu.sport.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.model.dto.response.EnumerationValueDto;
import ru.etu.sport.model.dto.response.ProductWithParamDto;

@Entity
@Table(name = "product_parameter")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_id", nullable = false)
    private Parameter parameter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enumeration_value_id")
    private EnumerationValue enumerationValue;

    @Column(name = "max_val")
    private Integer maxVal;

    @Column(name = "min_val")
    private Integer minVal;

    @Column(name = "int_val")
    private Integer intVal;

    public static List<ProductWithParamDto> mapProductParameter(List<ProductParameter> productParameters) {
        if (productParameters == null || productParameters.isEmpty()) {
            return List.of();
        }

        return productParameters.stream()
                .collect(Collectors.groupingBy(ProductParameter::getProduct))
                .entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    List<ProductParameter> paramsForProduct = entry.getValue();

                    // 2. Создаем списки для агрегации всех характеристик текущего продукта
                    List<EnumerationValueDto> enumValues = new ArrayList<>();
                    List<Integer> maxVals = new ArrayList<>();
                    List<Integer> minVals = new ArrayList<>();
                    List<Integer> intVals = new ArrayList<>();

                    // 3. Проходим по всем параметрам этого продукта и заполняем списки
                    for (ProductParameter pp : paramsForProduct) {
                        enumValues.add(EnumerationValue.mapEnumerationValue(pp.getEnumerationValue()));
                        maxVals.add(pp.getEnumerationValue() != null ? null : pp.getMaxVal());
                        minVals.add(pp.getEnumerationValue() != null ? null : pp.getMinVal());
                        intVals.add(pp.getEnumerationValue() != null ? null : pp.getIntVal());
                    }

                    // 4. Собираем итоговый ProductWithParamDto, где продукт уникален
                    return ProductWithParamDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .shortName(product.getShortName())
                            .classId(product.getProductClass() != null ?
                                    product.getProductClass().getId() : null)
                            .paramEnumValue(enumValues) // Передаем собранные списки
                            .maxVal(maxVals)
                            .minVal(minVals)
                            .intVal(intVals)
                            .build();
                })
                .collect(Collectors.toList());
    }
}