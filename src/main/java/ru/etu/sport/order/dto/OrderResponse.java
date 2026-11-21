package ru.etu.sport.order.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import ru.etu.sport.order.model.Order;
import ru.etu.sport.order.model.OrderItem;
import ru.etu.sport.order.model.OrderStatus;
import ru.etu.sport.organization.dto.OrganizationResponse;

public record OrderResponse(
    Long id,
    OrganizationResponse organization,
    OrderStatus status,
    List<OrderItemResponse> items,
    BigDecimal cost,
    ZonedDateTime createdAt
) {
    public static OrderResponse fromEntity(Order e) {
        BigDecimal cost = BigDecimal.ZERO;
        List<OrderItem> items = e.getItems();

        items.sort(Comparator.comparingLong((orderItem) -> orderItem.getPositionNumber()));

        for (OrderItem item : items) {
            cost = cost.add(
                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        return new OrderResponse(
            e.getId(),
            OrganizationResponse.fromEntity(e.getOrganization()), 
            e.getStatus(), 
            items.stream().map(item -> OrderItemResponse.fromEntity(item)).toList(), 
            cost,
            e.getCreatedAt()
        );
    } 
}
