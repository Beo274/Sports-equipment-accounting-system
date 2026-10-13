package ru.etu.sport.change.entities.dto;

import jakarta.persistence.*;
import ru.etu.sport.change.entities.ProductChange;
import ru.etu.sport.model.entity.Product;

import java.time.LocalDate;

public record ProductChangeResponse(
    Integer id,
    LocalDate date,
    Integer product,
    String description
) {
    public static ProductChangeResponse from(ProductChange change) {
        return new ProductChangeResponse(change.getId(),
                change.getDate(),
                change.getProduct().getId(),
                change.getDescription()
                );
    }
}
