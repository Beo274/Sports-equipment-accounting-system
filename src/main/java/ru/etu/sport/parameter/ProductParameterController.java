package ru.etu.sport.parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.etu.sport.model.dto.request.ProductParamBindingDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "product parameters", description = "Managing product parameters")
public class ProductParameterController {

    private final ClassProductParameterService service;

    @PostMapping("/param/product")
    public ResponseEntity<IdResponse> create(@RequestBody ProductParamBindingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(service.createProductParam(dto)));
    }

    @GetMapping("/param/product")
    public ResponseEntity<Map<String, Object>> getProductParams(@RequestParam(required = false) Integer productId) {
        if (productId == null) {
            return ResponseEntity.ok(Map.of("items", service.getAllProductParams()));
        } else {
            return ResponseEntity.ok(Map.of("items", service.getProductParams(productId)));
        }
    }

    @DeleteMapping("/param/product/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        service.deleteProductParam(id);
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @PatchMapping("/param/product/{id}")
    public ResponseEntity<MessageResponse> update(@PathVariable Integer id, @RequestBody ProductParamBindingDto dto) {
        service.updateProductParam(id, dto);
        return ResponseEntity.ok(new MessageResponse("updated"));
    }

    @GetMapping("/param/{id}/products")
    public ResponseEntity<Map<String, Object>> getProductsByParamValue(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getProductsByParamValue(id));
    }
}