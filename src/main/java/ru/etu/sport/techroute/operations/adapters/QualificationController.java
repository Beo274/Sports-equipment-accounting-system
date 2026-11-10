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
import ru.etu.sport.techroute.operations.dto.QualificationResponse;
import ru.etu.sport.techroute.operations.infrastructure.repository.QualificationRepository;
import ru.etu.sport.techroute.operations.persistence.Qualification;

@RestController
@Tag(name = "qualifications", description = "qualifications management")
@RequestMapping("/qualifications")
@RequiredArgsConstructor
public class QualificationController {

	private final QualificationRepository repository;

	@GetMapping
	public ResponseEntity<List<QualificationResponse>> getAll() {
		List<QualificationResponse> result = repository.findAll()
				.stream()
				.map(QualificationResponse::fromEntity)
				.toList();

		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<QualificationResponse> getById(
			@PathVariable Long id) {
		Qualification entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Qualification not found: " + id));

		return ResponseEntity.ok(QualificationResponse.fromEntity(entity));
	}

	@PostMapping
	public ResponseEntity<QualificationResponse> create(@Valid @RequestBody BaseCreateRequestDto dto) {
		Qualification qualification = Qualification.builder()
				.name(dto.getName())
				.description(dto.getDescription())
				.build();

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(QualificationResponse.fromEntity(repository.save(qualification)));
	}
}