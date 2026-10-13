package ru.etu.sport.change.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.change.entities.dto.ProductChangeRequest;
import ru.etu.sport.change.entities.dto.ProductChangeResponse;
import ru.etu.sport.change.entities.dto.UpdateProductChangeRequest;
import ru.etu.sport.change.services.ChangeService;

import java.util.List;

@RestController
@RequestMapping("modification/change")
@Tag(name = "BOM changes managing", description = "Debug controller for testing changes functions")
@Slf4j
public class ProductChangeController {

    private final ChangeService changeService;

    ProductChangeController(ChangeService changeService) {
        this.changeService = changeService;
    }

    @PostMapping
    public ResponseEntity<ProductChangeResponse> create(@RequestBody ProductChangeRequest request) {
        log.debug("Create product change: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(changeService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductChangeResponse> update(@PathVariable Long id, @RequestBody UpdateProductChangeRequest request) {
        log.debug("Update product change: {}", request);
        return ResponseEntity.ok(changeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductChangeResponse> delete(@PathVariable Long id) {
        log.debug("Delete product change: {}", id);
        changeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductChangeResponse>> findAll() {
        log.debug("Find all product change");
        return ResponseEntity.ok(changeService.getAll());
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<List<ProductChangeResponse>> findForProduct(@PathVariable Long product_id) {
        log.debug("Find changes for product: {}", product_id);
        return ResponseEntity.ok(changeService.getForProduct(product_id));

    }
}
