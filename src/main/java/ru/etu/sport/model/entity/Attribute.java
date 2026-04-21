package ru.etu.sport.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attribute")
@Data
@NoArgsConstructor
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "class_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ClassEntity classId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "int_value")
    private Integer intValue;

    @Column(name = "image_value")
    private String imageValue;

    @Column(name = "position")
    private Integer position;

    @JoinColumn(name = "measure_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Measure measure;
}