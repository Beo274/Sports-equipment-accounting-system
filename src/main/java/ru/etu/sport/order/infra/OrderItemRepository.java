package ru.etu.sport.order.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.order.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
}
