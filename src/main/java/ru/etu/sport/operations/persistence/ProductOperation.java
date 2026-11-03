package ru.etu.sport.operations.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import ru.etu.sport.model.entity.Product;

@Entity
@Table(name = "product_operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    Product product;

    @Column(name = "operation_number", nullable = false)
    @Builder.Default
    Long operationNumber = 1L;

    @Column(name = "name", nullable = false)
    String name;

    @JoinColumn(name = "operation_type_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    OperationType operationType;

    @JoinColumn(name = "workcenter_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    WorkCenter workCenter;

    @JoinColumn(name = "profession_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    Profession profession;

    @JoinColumn(name = "qualification_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    Qualification qualification;

    // Подготовительное время в миллисекундах
    @Column(name = "setup_time", nullable = false)
    @Builder.Default
    Long setupTime = 0L;

    // Так же в миллисекундах
    @Column(name = "piece_time", nullable = false)
    @Builder.Default
    Long pieceTime = 0L;

    @Column(name = "description")
    @Builder.Default
    String description = "";
}
