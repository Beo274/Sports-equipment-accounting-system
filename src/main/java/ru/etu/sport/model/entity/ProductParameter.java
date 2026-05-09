package ru.etu.sport.model.entity;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.model.dto.response.ProductWithParamDto;

@Entity
@Table(name = "product_parameter")
@Data
@NoArgsConstructor
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
        return productParameters.stream()
                .map(pp -> {
                    Product product = pp.getProduct();
                    return ProductWithParamDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .shortName(product.getShortName())
                            .classId(product.getProductClass() != null ? 
                                    product.getProductClass().getId() : null)
                            .paramEnumValue(EnumerationValue.mapEnumerationValue(pp.getEnumerationValue()))
                            .maxVal(pp.getEnumerationValue() != null ? null : pp.getMaxVal())
                            .minVal(pp.getEnumerationValue() != null ? null : pp.getMinVal())
                            .intVal(pp.getEnumerationValue() != null ? null : pp.getIntVal())
                            .build();
                })
                .collect(Collectors.toList());
    }
}