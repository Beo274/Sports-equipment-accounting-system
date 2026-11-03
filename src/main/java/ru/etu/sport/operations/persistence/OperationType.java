package ru.etu.sport.operations.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operation_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationType {
    @Id
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    @Builder.Default
    String description = "";
}
