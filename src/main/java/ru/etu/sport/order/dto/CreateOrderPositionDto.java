package ru.etu.sport.order.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderPositionDto {
    Long productId;

    BigDecimal unitPrice;

    Long quantity;
}
