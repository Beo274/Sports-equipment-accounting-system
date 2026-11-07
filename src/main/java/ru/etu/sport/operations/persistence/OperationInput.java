package ru.etu.sport.operations.persistence;

import java.math.BigDecimal;

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
import ru.etu.sport.model.entity.Measure;

@Entity
@Table(name = "operation_input")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Операция - потребитель ресурса (куда ресурс пойдёт дальше)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_operation_id", nullable = false)
    ProductOperation outputOperation;

    // Операция, результат которой потребляется
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_operation_id", nullable = false)
    ProductOperation inputOperation;

    // Норма расхода
    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    BigDecimal quantity;

    // База расхода - количество выходного продукта, на которое дана норма. Похоже
    // на for_quantity в bom
    @Column(name = "base_quantity", nullable = false, precision = 19, scale = 6)
    @Builder.Default
    BigDecimal baseQuantity = BigDecimal.ONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_unit_id", nullable = false)
    Measure measure;
}
