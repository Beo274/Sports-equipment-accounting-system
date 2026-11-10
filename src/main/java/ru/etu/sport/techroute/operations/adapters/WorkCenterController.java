package ru.etu.sport.techroute.operations.adapters;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.techroute.operations.dto.BaseCreateRequestDto;
import ru.etu.sport.techroute.operations.dto.WorkCenterResponse;
import ru.etu.sport.techroute.operations.infrastructure.repository.WorkCenterRepository;
import ru.etu.sport.techroute.operations.persistence.WorkCenter;

@RestController
@Tag(name = "work centers", description = "work centers management")
@RequestMapping("/work-centers")
@RequiredArgsConstructor
public class WorkCenterController {

    private final WorkCenterRepository repository;

    @GetMapping
    public ResponseEntity<List<WorkCenterResponse>> getAll() {
        List<WorkCenterResponse> result = repository.findAll()
                .stream()
                .map(WorkCenterResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkCenterResponse> getById(
            @PathVariable Long id) {
        WorkCenter entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work center not found: " + id));

        return ResponseEntity.ok(WorkCenterResponse.fromEntity(entity));
    }

    @PostMapping
    public ResponseEntity<WorkCenterResponse> create(@Valid @RequestBody BaseCreateRequestDto dto) {
        WorkCenter workCenter = WorkCenter.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WorkCenterResponse.fromEntity(repository.save(workCenter)));
    }
}
