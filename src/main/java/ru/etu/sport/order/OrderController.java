package ru.etu.sport.order;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.order.application.OrderService;
import ru.etu.sport.order.dto.CreateOrderDto;
import ru.etu.sport.order.dto.CreateOrderPositionDto;
import ru.etu.sport.order.dto.OrderItemResponse;
import ru.etu.sport.order.dto.OrderResponse;
import ru.etu.sport.order.dto.UpdateOrderPositionDto;
import ru.etu.sport.order.model.OrderStatus;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "orders", description = "orders management")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> list() {
        return ResponseEntity.ok(orderService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> changeStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.changeStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<OrderResponse> addPosition(@PathVariable Long id, @Valid @RequestBody CreateOrderPositionDto dto) {
        orderService.addPosition(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.getById(id));
    }

    @DeleteMapping("/{id}/{position}")
    public ResponseEntity<?> removePosition(@PathVariable Long id, @PathVariable Long position) {
        orderService.removePosition(id, position);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/organization/{id}")
    public ResponseEntity<List<OrderResponse>> getByOrganizationId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByOrganizationId(id));
    }
    
    @PatchMapping("/{id}/{position}")
    public ResponseEntity<OrderItemResponse> updatePosition(
        @PathVariable Long id, 
        @PathVariable Long position, 
        @Valid @RequestBody UpdateOrderPositionDto dto
    ) {
        return ResponseEntity.ok(orderService.updatePosition(id, position, dto));
    }
}
