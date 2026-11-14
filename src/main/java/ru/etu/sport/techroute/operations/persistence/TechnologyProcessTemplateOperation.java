package ru.etu.sport.techroute.operations.persistence;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
import ru.etu.sport.techroute.operations.domain.OperationKind;

@Entity
@Table(name = "technology_process_template_operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnologyProcessTemplateOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private TechnologyProcessTemplate template;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    @Builder.Default
    private String description = "";

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "kind", nullable = false)
    @Builder.Default
    private OperationKind kind = OperationKind.TECHNOLOGICAL;

    @Column(name = "operation_number", nullable = false)
    @Builder.Default
    private Long operationNumber = 1L;

    @Column(name = "setup_time", nullable = false)
    @Builder.Default
    private Long setupTime = 0L;

    @Column(name = "piece_time", nullable = false)
    @Builder.Default
    private Long pieceTime = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type_id")
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workcenter_id")
    private WorkCenter workCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_id")
    private Profession profession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;
}