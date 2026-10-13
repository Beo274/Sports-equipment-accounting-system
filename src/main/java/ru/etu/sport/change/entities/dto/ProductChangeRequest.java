package ru.etu.sport.change.entities.dto;


import lombok.Data;
import ru.etu.sport.model.entity.Product;

import java.time.LocalDate;

@Data
public class ProductChangeRequest {
    private LocalDate date;
    private Long product;
    private String description;
}
