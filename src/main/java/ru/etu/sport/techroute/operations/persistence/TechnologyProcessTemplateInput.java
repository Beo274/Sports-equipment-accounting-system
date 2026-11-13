package ru.etu.sport.techroute.operations.persistence;

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
@Table(name = "technology_process_template_input")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnologyProcessTemplateInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_template_operation_id", nullable = false)
    private TechnologyProcessTemplateOperation outputTemplateOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_template_operation_id")
    private TechnologyProcessTemplateOperation inputTemplateOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_input_operation_id")
    private ProductOperation externalInputOperation;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "base_quantity", nullable = false, precision = 19, scale = 6)
    @Builder.Default
    private BigDecimal baseQuantity = BigDecimal.ONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_unit_id")
    private Measure measure;
}