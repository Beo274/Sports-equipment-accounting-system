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
@Table(name = "enumeration_value")
@Data
@RequiredArgsConstructor
public class EnumerationValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @JoinColumn(name = "enumeration_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Enumeration enumeration;

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
