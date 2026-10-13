package ru.etu.sport.change.entities.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UpdateProductChangeRequest {
    @JsonSetter(nulls = Nulls.SKIP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @JsonSetter (nulls = Nulls.SKIP)
    @Positive (message = "ID must be positive")
    private Long product;

    @JsonSetter (nulls = Nulls.SKIP)
    private String description;
}
