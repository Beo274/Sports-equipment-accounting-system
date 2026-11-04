package ru.etu.sport.operations.adapters;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.operations.dto.BaseCreateRequestDto;
import ru.etu.sport.operations.dto.OperationTypeResponse;
import ru.etu.sport.operations.infrastructure.repository.OperationTypeRepository;
import ru.etu.sport.operations.persistence.OperationType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Tag(name = "operation type", description = "operation type management")
@RequestMapping("/operation-types")
@RequiredArgsConstructor
public class OperationTypeController {

	private final OperationTypeRepository repository;

	@GetMapping
	public ResponseEntity<List<OperationTypeResponse>> getAll() {
		List<OperationTypeResponse> result = repository.findAll()
				.stream()
				.map(OperationTypeResponse::fromEntity)
				.toList();

		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OperationTypeResponse> getById(
			@PathVariable Long id) {
		OperationType operationType = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						"Operation type not found: " + id));

		return ResponseEntity.ok(
				OperationTypeResponse.fromEntity(operationType));
	}

	@PostMapping
	public ResponseEntity<OperationTypeResponse> create(@Valid @RequestBody BaseCreateRequestDto dto) {
		OperationType operationType = OperationType.builder()
				.name(dto.getName())
				.description(dto.getDescription())
				.build();

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(OperationTypeResponse.fromEntity(repository.save(operationType)));
	}
}
