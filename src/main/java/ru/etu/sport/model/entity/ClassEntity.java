package ru.etu.sport.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "class")
@Data
@AllArgsConstructor
@Builder
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "short_name", length = 128, nullable = false)
    private String shortName;

    @Column(name = "base_class_id")
    private Integer baseClassId;

    @Column(name = "m_unit_id")
    private Integer mUnitId;

    @Transient
    private Integer level;
}
