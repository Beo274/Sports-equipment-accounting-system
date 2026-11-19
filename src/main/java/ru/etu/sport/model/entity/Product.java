package ru.etu.sport.model.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.etu.sport.product.model.ProductType;

@Entity
@Table(name = "Product")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @JoinColumn(name = "class_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ClassEntity productClass;

    @JoinColumn(name = "base_product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product baseProduct;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "product_type", nullable = false)
    private ProductType type = ProductType.SIMPLE;
}
