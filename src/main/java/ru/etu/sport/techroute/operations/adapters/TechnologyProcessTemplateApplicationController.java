package ru.etu.sport.techroute.operations.adapters;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.techroute.operations.application.TechnologyProcessTemplateService;
import ru.etu.sport.techroute.operations.dto.ProductOperationReponse;

@RestController
@Tag(name = "technology process template application", description = "Применение типового процесса к изделию")
@RequestMapping("/products/{productId}/technology-process-templates")
@RequiredArgsConstructor
public class TechnologyProcessTemplateApplicationController {

    private final TechnologyProcessTemplateService templateService;

    @PostMapping("/{templateId}/apply")
    public ResponseEntity<List<ProductOperationReponse>> applyTemplate(
            @PathVariable Long productId,
            @PathVariable Long templateId
    ) {
        List<ProductOperationReponse> createdOperations = templateService.applyToProduct(productId, templateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOperations);
    }
}