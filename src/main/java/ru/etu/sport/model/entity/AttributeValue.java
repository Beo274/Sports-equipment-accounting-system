package ru.etu.sport.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "attribute_value")
@Data
@RequiredArgsConstructor
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @JoinColumn(name = "attribute_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Attribute attribute;

    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "short_name", length = 128, nullable = false)
    private String shortName;

    @Column(name = "int_value")
    private Integer intValue;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "image_value")
    private String imageValue;

    @Column(name = "position")
    private Integer position;

    @JoinColumn(name = "measure_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Measure measure;
}
