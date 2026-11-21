package ru.etu.sport.order.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderPositionDto {
    @Positive
    Long quantity;

    @Positive
    BigDecimal unitPrice;
}
