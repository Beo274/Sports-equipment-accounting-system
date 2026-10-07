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
import ru.etu.sport.model.dto.response.EnumerationValueDto;

@Entity
@Table(name = "enumeration_value")
@Data
@RequiredArgsConstructor
public class EnumerationValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    public static EnumerationValueDto mapEnumerationValue(EnumerationValue ev)  {
         if (ev == null) {
            return null;
        }
        
        return EnumerationValueDto.builder()
                .id(ev.getId())
                .enumerationId(ev.getEnumeration().getId())
                .intValue(ev.getIntValue())
                .stringValue(ev.getStringValue())
                .imageValue(ev.getImageValue())
                .position(ev.getPosition())
                .measureId(ev.getMeasure() != null ? ev.getMeasure().getId() : null)
                .build();
    }
}
