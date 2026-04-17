package ru.etu.sport.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "class")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "short_name", length = 128, nullable = false)
    private String shortName;

    @JoinColumn(name = "base_class_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ClassEntity baseClassId;

    @JoinColumn(name = "m_unit_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Measure mUnitId;

    @Transient
    private Integer level;
}
