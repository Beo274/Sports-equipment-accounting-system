package ru.etu.sport.operations.adapters;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.operations.application.TechnologyRouteService;
import ru.etu.sport.operations.dto.CreateOperationInputDto;
import ru.etu.sport.operations.dto.OperationInputResponse;

@RestController
@RequestMapping("/product-operations/{operationId}/inputs")
@RequiredArgsConstructor
public class OperationInputController {

    private final TechnologyRouteService technologyRouteService;
    
    @PostMapping
    public ResponseEntity<OperationInputResponse> addInput(
        @PathVariable Long operationId,
        @RequestBody CreateOperationInputDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(technologyRouteService.addInput(operationId, dto));
    }

    @GetMapping
    public ResponseEntity<List<OperationInputResponse>> getInputs(@PathVariable Long operationId) {
        return ResponseEntity.ok(technologyRouteService.getInputs(operationId));
    }
    
    @PutMapping("/{inputId}")
    public ResponseEntity<OperationInputResponse> changeInput(
        @PathVariable("operationId") Long operationId, 
        @PathVariable("inputId") Long inputId,
        @Valid @RequestBody CreateOperationInputDto dto
    ) {
        return ResponseEntity.ok(technologyRouteService.updateInput(operationId, inputId, dto));
    }

    @DeleteMapping("/{inputId}")
    public ResponseEntity<Void> deleteInput(
        @PathVariable("operationId") Long operationId,
        @PathVariable("inputId") Long inputId
    ) {
        technologyRouteService.deleteInput(operationId, inputId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
