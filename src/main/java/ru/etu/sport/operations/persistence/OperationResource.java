package ru.etu.sport.operations.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.operations.dto.ResourceType;

@Entity
@Table(name = "operation_resource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_operation_id", nullable = false)
    private ProductOperation productOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Product resource;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 64)
    private ResourceType resourceType;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Long quantity = 1L;

    @Column(name = "base_quantity", nullable = false)
    @Builder.Default
    private Long baseQuantity = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_unit_id", nullable = false)
    private Measure unit;
}