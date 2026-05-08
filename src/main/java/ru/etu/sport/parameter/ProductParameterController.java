package ru.etu.sport.parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.dto.request.ParamBindingDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductParameterController {

    private final ClassProductParameterService service;

    @PostMapping("/param/product")
    public ResponseEntity<IdResponse> create(@RequestBody ParamBindingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(service.createProductParam(dto)));
    }

    @GetMapping("/param/product")
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of("items", service.getAllProductParams()));
    }

    @DeleteMapping("/param/product/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        service.deleteProductParam(id);
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @PatchMapping("/param/product/{id}")
    public ResponseEntity<MessageResponse> update(@PathVariable Integer id, @RequestBody ParamBindingDto dto) {
        service.updateProductParam(id, dto);
        return ResponseEntity.ok(new MessageResponse("updated"));
    }

    @GetMapping("/product/params")
    public ResponseEntity<Map<String, Object>> getProductsWithParams(@RequestParam("class_id") Integer classId) {
        return ResponseEntity.ok(service.getProductsWithParamsByClass(classId));
    }

    @GetMapping("/param/{id}/products")
    public ResponseEntity<Map<String, Object>> getProductsByParamValue(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getProductsByParamValue(id));
    }
}