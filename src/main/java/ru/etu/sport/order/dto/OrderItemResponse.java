package ru.etu.sport.order.dto;

import java.math.BigDecimal;

import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.order.model.OrderItem;

public record OrderItemResponse(
    Long id,
    ProductResponse product,
    Long quantity,
    BigDecimal unitPrice,
    Long position
) {
    public static OrderItemResponse fromEntity(OrderItem e) {
        return new OrderItemResponse(
            e.getId(), 
            ProductResponse.from(e.getProduct()), 
            e.getQuantity(),
            e.getUnitPrice(), 
            e.getPositionNumber());
    }
}
