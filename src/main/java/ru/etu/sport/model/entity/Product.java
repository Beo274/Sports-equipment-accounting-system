package ru.etu.sport.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    @OneToOne(fetch = FetchType.LAZY)
    private ClassEntity productClass;

    @JoinColumn(name = "base_product_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Product baseProduct;
}
