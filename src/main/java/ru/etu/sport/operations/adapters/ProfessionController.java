package ru.etu.sport.operations.adapters;

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
import ru.etu.sport.operations.dto.BaseCreateRequestDto;
import ru.etu.sport.operations.dto.ProfessionResponse;
import ru.etu.sport.operations.infrastructure.repository.ProfessionRepository;
import ru.etu.sport.operations.persistence.Profession;

@RestController
@Tag(name = "professions", description = "professions management")
@RequestMapping("/professions")
@RequiredArgsConstructor
public class ProfessionController {

	private final ProfessionRepository repository;

	@GetMapping
	public ResponseEntity<List<ProfessionResponse>> getAll() {
		List<ProfessionResponse> result = repository.findAll()
				.stream()
				.map(ProfessionResponse::fromEntity)
				.toList();

		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProfessionResponse> getById(
			@PathVariable Long id) {
		Profession entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Profession not found: " + id));

		return ResponseEntity.ok(ProfessionResponse.fromEntity(entity));
	}

	@PostMapping
	public ResponseEntity<ProfessionResponse> create(@Valid @RequestBody BaseCreateRequestDto dto) {
		Profession profession = Profession.builder()
				.name(dto.getName())
				.description(dto.getDescription())
				.build();

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ProfessionResponse.fromEntity(repository.save(profession)));
	}
}