package ru.etu.sport.enumeration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.request.CreateEnumerationDto;
import ru.etu.sport.model.dto.request.CreateEnumerationValueDto;
import ru.etu.sport.model.dto.request.ReorderDto;
import ru.etu.sport.model.dto.request.ValueDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;
import ru.etu.sport.model.entity.Enumeration;
import ru.etu.sport.model.entity.EnumerationValue;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/enumeration")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "enumerations", description = "Enumeration managing endpoints")
public class EnumerationController {

    private final EnumerationService enumerationService;

    @PostMapping
    public ResponseEntity<IdResponse> createEnumeration(@Valid @RequestBody CreateEnumerationDto createEnumerationDto) {
        Enumeration enumeration = this.enumerationService.create(createEnumerationDto);
        log.info("Enumeration created");
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(enumeration.getId()));
    }
    
    @PostMapping("/value")
    public ResponseEntity<IdResponse> creatEnumerationValue(@Valid @RequestBody CreateEnumerationValueDto createEnumerationValueDto) {
        EnumerationValue value = this.enumerationService.createValue(createEnumerationValueDto);
        log.info("Value for enumeration with id: {} created", createEnumerationValueDto.getEnumerationId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(value.getId()));
    }
    
    @PatchMapping("/value/{id}")
    public ResponseEntity<MessageResponse> updateValue(@Valid @RequestBody ValueDto valueDto, @PathVariable("id") Integer id) {
        valueDto.updateValue(enumerationService, id);
        log.info("Value with id: {} updated", id);
        return ResponseEntity.ok(new MessageResponse("updated"));
    }

    @GetMapping("/{id}/values")
    public ResponseEntity<List<EnumerationValue>> getEnumerationValues(@PathVariable("id") Integer id) {
        List<EnumerationValue> values = this.enumerationService.listEnumerationValues(id);
        log.info("Values for enumeration with id: {} provided", id);
        return ResponseEntity.ok(values);
    }

    @GetMapping
    public ResponseEntity<List<Enumeration>> getEnumerations() {
        List<Enumeration> list = this.enumerationService.listEnumerations();
        log.info("Enumerations list provided");
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/value/{id}")
    public ResponseEntity<EnumerationValue> getEnumerationValue(@PathVariable Integer id) {
        EnumerationValue value = this.enumerationService.getEnumerationValue(id);
        log.info("Value by id: {} provided", id);
        return ResponseEntity.ok(value);
    }
    
    @PatchMapping("/reorder")
    public ResponseEntity<MessageResponse> reorderValues(@RequestBody ReorderDto reorderDto) {
        this.enumerationService.reorderValues(reorderDto.getOrder());
        log.info("Values reordered");
        return ResponseEntity.ok(new MessageResponse("reordered"));
    }

    @DeleteMapping("/value/{id}")
    public ResponseEntity<MessageResponse> deleteEnumerationValue(@PathVariable("id") Integer id, @RequestParam DeleteValueOption option) {
        this.enumerationService.deleteValue(id, option);
        log.info("Enumeration value deleted, option: {}", option);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MessageResponse("deleted"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteEnumeration(@PathVariable("id") Integer id) {
        this.enumerationService.deleteEnumeration(id);
        log.info("Enumeration deleted");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new MessageResponse("deleted"));
    }
}