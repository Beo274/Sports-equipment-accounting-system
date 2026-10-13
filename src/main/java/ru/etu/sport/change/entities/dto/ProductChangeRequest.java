package ru.etu.sport.change.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProductChangeRequest {
    private LocalDate date;
    private Long product;
    private String description;
}
