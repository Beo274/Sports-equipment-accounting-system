package ru.etu.sport.organization;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.organization.dto.CreateOrganizationDto;
import ru.etu.sport.organization.dto.OrganizationResponse;
import ru.etu.sport.organization.model.Organization;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor 
@Tag(name = "organization", description = "organizations management")
public class OrganizationController {
    private final OrganizationReporitory organizationRepository;

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> list() {
        List<OrganizationResponse> result = organizationRepository
            .findAll().stream()
            .map(org -> new OrganizationResponse(org.getId(), org.getName()))
            .toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> add(@Valid @RequestBody CreateOrganizationDto dto) {
        Organization organization = new Organization();
        organization.setName(dto.getName());
        
        organization = organizationRepository.save(organization);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new OrganizationResponse(organization.getId(), organization.getName())
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getById(@PathVariable Long id) {
        Organization organization = organizationRepository.getReferenceById(id);

        return ResponseEntity.ok(new OrganizationResponse(id, organization.getName()));
    }
    
}
