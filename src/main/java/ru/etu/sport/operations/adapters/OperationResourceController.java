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
import ru.etu.sport.operations.application.OperationResourceService;
import ru.etu.sport.operations.dto.OperationResourceDto;
import ru.etu.sport.operations.dto.OperationResourceResponse;
import ru.etu.sport.operations.dto.UpdateOperationResourceDto;

@RestController
@Tag(name = "operation resource", description = "operation resource management")
@RequestMapping({"/api/operation-resources", "/operation-resources"})
@RequiredArgsConstructor
public class OperationResourceController {

    private final OperationResourceService operationResourceService;

    @PostMapping
    public ResponseEntity<OperationResourceResponse> create(
            @Valid @RequestBody OperationResourceDto dto) {
        OperationResourceResponse response = operationResourceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationResourceResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(operationResourceService.getById(id));
    }

    @GetMapping("/operation/{operationId}")
    public ResponseEntity<List<OperationResourceResponse>> getByOperationId(
            @PathVariable Long operationId) {
        return ResponseEntity.ok(operationResourceService.getByOperationId(operationId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OperationResourceResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateOperationResourceDto dto) {
        return ResponseEntity.ok(operationResourceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        operationResourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}