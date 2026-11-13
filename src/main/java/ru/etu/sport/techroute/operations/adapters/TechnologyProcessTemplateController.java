package ru.etu.sport.techroute.operations.adapters;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
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

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.techroute.operations.application.TechnologyProcessTemplateService;
import ru.etu.sport.techroute.operations.dto.CreateTechnologyProcessTemplateDto;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateResponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateShortResponse;

@RestController
@Tag(name = "technology process templates", description = "Управление типовыми технологическими процессами")
@RequestMapping("/technology-process-templates")
@RequiredArgsConstructor
public class TechnologyProcessTemplateController {

    private final TechnologyProcessTemplateService templateService;

    @PostMapping("/from-product/{productId}")
    @Operation(summary = "Создать шаблон на основе маршрута изделия")
    public ResponseEntity<TechnologyProcessTemplateResponse> createFromProduct(
            @PathVariable Long productId,
            @RequestBody(required = false) CreateTechnologyProcessTemplateDto dto
    ) {
        TechnologyProcessTemplateResponse response = templateService.createFromProduct(productId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить список шаблонов")
    public ResponseEntity<List<TechnologyProcessTemplateShortResponse>> getAll() {
        return ResponseEntity.ok(templateService.getAll());
    }

    @GetMapping("/{templateId}")
    @Operation(summary = "Получить шаблон со всеми операциями")
    public ResponseEntity<TechnologyProcessTemplateResponse> getById(
            @PathVariable Long templateId
    ) {
        return ResponseEntity.ok(templateService.getById(templateId));
    }

    @PutMapping("/{templateId}")
    @Operation(summary  = "Изменить название и описание шаблона")
    public ResponseEntity<TechnologyProcessTemplateResponse> update(
            @PathVariable Long templateId,
            @RequestBody CreateTechnologyProcessTemplateDto dto
    ) {
        return ResponseEntity.ok(templateService.update(templateId, dto));
    }

    @DeleteMapping("/{templateId}")
    @Operation(summary  = "Удалить шаблон")
    public ResponseEntity<Void> delete(
            @PathVariable Long templateId
    ) {
        templateService.delete(templateId);
        return ResponseEntity.noContent().build();
    }
}