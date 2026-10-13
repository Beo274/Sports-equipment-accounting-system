package ru.etu.sport.bom.adapters;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.bom.application.BomService;
import ru.etu.sport.bom.dto.BomResponse;
import ru.etu.sport.bom.dto.CreateBomRequest;
import ru.etu.sport.bom.dto.UpdateBomRequest;

@RestController
@RequestMapping("/bom")
@RequiredArgsConstructor
@Tag(name = "bom", description = "Managing bom specification")
@Slf4j 
public class BomController {
    private final BomService bomService;

    @PostMapping 
    public ResponseEntity<BomResponse> create(@Valid @RequestBody CreateBomRequest request) {
        log.debug("Create bom request: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bomService.create(request));
    }

    // Частичное обновление
    @PatchMapping ("/{id}")
    public ResponseEntity<BomResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateBomRequest request
    ) {
        log.debug("Patch bom request: {}", request);
        return ResponseEntity.ok(bomService.update(id, request));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Integer id) {
        log.debug("Remove bom request for id: {}", id);
        bomService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping 
    public ResponseEntity<List<BomResponse>> list(
            @RequestParam (required = false) List<Integer> ids
    ) {
        log.debug("List bom request");
        List<BomResponse> result = (ids == null || ids.isEmpty())
                ? bomService.list()
                : bomService.list(ids);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BomResponse> getById(@PathVariable Integer id) {
        log.debug("Get bom request for id: {}", id);
        return ResponseEntity.ok(bomService.getById(id));
    }
}
