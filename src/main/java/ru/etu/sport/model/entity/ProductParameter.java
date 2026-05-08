package ru.etu.sport.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}