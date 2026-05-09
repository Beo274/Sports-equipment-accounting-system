package ru.etu.sport.model.entity;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.model.dto.response.ClassWithParamDto;

@Entity
@Table(name = "class_parameter")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_id", nullable = false)
    private Parameter parameter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enumeration_value_id")
    private EnumerationValue enumerationValue;

    @Column(name = "max_val")
    private Integer maxVal;

    @Column(name = "min_val")
    private Integer minVal;

    @Column(name = "int_val")
    private Integer intVal;

    public static List<ClassWithParamDto> mapClassParameter(List<ClassParameter> classParameters) {
        return classParameters.stream()
            .map(cp -> {
                ClassEntity classEntity = cp.getClassEntity();
                return ClassWithParamDto.builder()
                    .id(classEntity.getId())
                    .name(classEntity.getName())
                    .shortName(classEntity.getShortName())
                    .baseClassId(classEntity.getBaseClass() != null ?
                        classEntity.getBaseClass().getId() : null
                    )
                    .paramEnumValue(EnumerationValue.mapEnumerationValue(cp.getEnumerationValue()))
                    .maxVal(cp.getEnumerationValue() != null ? null : cp.getMaxVal())
                    .minVal(cp.getEnumerationValue() != null ? null : cp.getMinVal())
                    .intVal(cp.getEnumerationValue() != null ? null : cp.getIntVal())
                    .build();
            })
            .collect(Collectors.toList());
    }
}