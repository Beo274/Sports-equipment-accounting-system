package ru.etu.sport.order.application;

import java.util.List;

import ru.etu.sport.order.dto.CreateOrderDto;
import ru.etu.sport.order.dto.CreateOrderPositionDto;
import ru.etu.sport.order.dto.OrderItemResponse;
import ru.etu.sport.order.dto.OrderResponse;
import ru.etu.sport.order.dto.UpdateOrderPositionDto;
import ru.etu.sport.order.model.OrderStatus;

public interface OrderService {
    OrderResponse create(CreateOrderDto dto);

    List<OrderResponse> getAll();

    OrderResponse getById(Long id);

    List<OrderResponse> getByOrganizationId(Long id);

    OrderResponse changeStatus(Long id, OrderStatus status);

    void delete(Long id);

    OrderItemResponse addPosition(Long orderId, CreateOrderPositionDto dto);

    void removePosition(Long orderId, Long positionNumber);

    OrderItemResponse updatePosition(Long orderId, Long positionNumber, UpdateOrderPositionDto dto);
}
