package ru.etu.sport.order.application;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.order.dto.CreateOrderDto;
import ru.etu.sport.order.dto.CreateOrderPositionDto;
import ru.etu.sport.order.dto.OrderItemResponse;
import ru.etu.sport.order.dto.OrderResponse;
import ru.etu.sport.order.dto.UpdateOrderPositionDto;
import ru.etu.sport.order.infra.OrderItemRepository;
import ru.etu.sport.order.infra.OrderRepository;
import ru.etu.sport.order.model.Order;
import ru.etu.sport.order.model.OrderItem;
import ru.etu.sport.order.model.OrderStatus;
import ru.etu.sport.organization.OrganizationReporitory;
import ru.etu.sport.organization.model.Organization;
import ru.etu.sport.product.ProductRepository;
import ru.etu.sport.product.model.ProductType;

@Service
@RequiredArgsConstructor
public class OrderSerivceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrganizationReporitory organizationReporitory;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse changeStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        
        order.setStatus(status);

        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional
    public OrderResponse create(CreateOrderDto dto) {
        Organization organization = organizationReporitory.findById(dto.getOrganizationId())
            .orElseThrow(() -> new EntityNotFoundException("Organization not found: " + dto.getOrganizationId()));

        Order order = new Order();
        order.setOrganization(organization);

        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderResponse> getAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> OrderResponse.fromEntity(order)).toList();
    }

    @Override
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        return OrderResponse.fromEntity(order);
    }

    @Override
    public List<OrderResponse> getByOrganizationId(Long id) {
        Organization organization = organizationReporitory.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Organization not found: " + id));

        List<Order> orders = orderRepository.findByOrganization(organization);

        return orders.stream().map(order -> OrderResponse.fromEntity(order)).toList();
    }
    
    @Override
    @Transactional
    public OrderItemResponse addPosition(Long orderId, CreateOrderPositionDto dto) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (
            order.getStatus() == OrderStatus.CANCELLED || 
            order.getStatus() == OrderStatus.DONE
        ) {
            throw new ValidationException("Cannot add item to closed order");
        }
        
        OrderItem orderItem = new OrderItem();

        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new EntityNotFoundException("Product not found: " + dto.getProductId()));

        if (product.getType() == ProductType.CONFIGURABLE) {
            throw new ValidationException("Cannot add a configurable product to order");
        }

        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setUnitPrice(dto.getUnitPrice());

        orderItem = order.addItem(orderItem);

        return OrderItemResponse.fromEntity(orderItem);
    }

    @Override
    @Transactional
    public void removePosition(Long orderId, Long positionNumber) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (
            order.getStatus() == OrderStatus.CANCELLED || 
            order.getStatus() == OrderStatus.DONE
        ) {
            throw new ValidationException("Cannot remove item from a closed order");
        }

        try {
            order.removeItem(positionNumber, orderItemRepository);
        } catch (IndexOutOfBoundsException error) {
            throw new EntityNotFoundException("Position not found: " + positionNumber);
        }
    }

    @Override
    @Transactional
    public OrderItemResponse updatePosition(Long orderId, Long positionNumber, UpdateOrderPositionDto dto) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        try {
            OrderItem item = order.getItem(positionNumber);
            if (dto.getQuantity() != null) {
                item.setQuantity(dto.getQuantity());
            }

            if (dto.getUnitPrice() != null) {
                item.setUnitPrice(dto.getUnitPrice());
            }
            return OrderItemResponse.fromEntity(item);
        } catch (IndexOutOfBoundsException error) {
            throw new EntityNotFoundException("Position not found: " + positionNumber);
        } 
    }
}
