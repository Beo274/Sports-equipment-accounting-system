package ru.etu.sport.operations.adapters;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.operations.application.ProductOperationService;
import ru.etu.sport.operations.dto.ProductOperationDto;
import ru.etu.sport.operations.dto.ProductOperationReponse;
import ru.etu.sport.operations.dto.UpdateProductOperationDto;

@RestController
@Tag(name = "product operation", description = "product operation management")
@RequestMapping("/product-operations")
@RequiredArgsConstructor
public class ProductOperationController {

    private final ProductOperationService productOperationService;

    @PostMapping
    public ResponseEntity<ProductOperationReponse> create(
            @Valid @RequestBody ProductOperationDto dto) {
        ProductOperationReponse response = productOperationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOperationReponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(productOperationService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductOperationReponse>> getByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
                productOperationService.getByProductId(productId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductOperationReponse> update(
            @PathVariable Long id,
            @RequestBody UpdateProductOperationDto dto) {
        return ResponseEntity.ok(productOperationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        productOperationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}