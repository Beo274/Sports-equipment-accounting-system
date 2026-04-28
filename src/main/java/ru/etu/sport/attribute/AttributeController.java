package ru.etu.sport.attribute;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.request.CreateAttributeDto;
import ru.etu.sport.model.dto.request.CreateAttributeValueDto;
import ru.etu.sport.model.dto.request.ReorderDto;
import ru.etu.sport.model.dto.request.ValueDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.entity.Attribute;
import ru.etu.sport.model.entity.AttributeValue;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/attribute")
@RequiredArgsConstructor
@Slf4j
public class AttributeController {

    private final AttributeService attributeService;

    @PostMapping
    public ResponseEntity<IdResponse> createAttribute(@Valid @RequestBody CreateAttributeDto createAttributeDto) {
        Attribute attribute = this.attributeService.create(createAttributeDto);
        log.info("Attribute created");
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(attribute.getId()));
    }
    
    @PostMapping("/value")
    public ResponseEntity<IdResponse> createAttributeValue(@Valid @RequestBody CreateAttributeValueDto createAttributeValueDto) {
        AttributeValue value = this.attributeService.createValue(createAttributeValueDto);
        log.info("Value for attribute with id: {} created", createAttributeValueDto.getAttributeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(value.getId()));
    }
    
    @PatchMapping("/value/{id}")
    public ResponseEntity<Map<String, String>> updateValue(@Valid @RequestBody ValueDto valueDto, @PathVariable("id") Integer id) {
        valueDto.updateValue(attributeService, id);
        log.info("Value with id: {} updated", id);
        return ResponseEntity.ok(Map.of("message", "updated"));
    }

    @GetMapping("/{id}/values")
    public ResponseEntity<List<AttributeValue>> getAttributeValues(@PathVariable("id") Integer id) {
        List<AttributeValue> values = this.attributeService.listAttributeValues(id);
        log.info("Values for attribute with id: {} provided", id);
        return ResponseEntity.ok(values);
    }

    @GetMapping
    public ResponseEntity<List<Attribute>> getAttributes() {
        List<Attribute> list = this.attributeService.listAttributes();
        log.info("Attributes list provided");
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/value/{id}")
    public ResponseEntity<AttributeValue> getAttributeValue(@PathVariable Integer id) {
        AttributeValue value = this.attributeService.getAttributeValue(id);
        log.info("Value by id: {} provided", id);
        return ResponseEntity.ok(value);
    }
    
    @PatchMapping("/{id}/reorder")
    public ResponseEntity<Map<String, String>> reorderValues(@RequestBody ReorderDto reorderDto) {
        this.attributeService.reorderValues(reorderDto.getOrder());
        log.info("Values reordered");
        return ResponseEntity.ok(Map.of("message", "reordered"));
    }

    @DeleteMapping("/value/{id}")
    public ResponseEntity<Map<String, String>> deleteAttributeValue(@PathVariable("id") Integer id, @RequestParam DeleteValueOption option) {
        this.attributeService.deleteValue(id, option);
        log.info("Attribute value deleted, option: {}", option);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "deleted"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAttribute(@PathVariable("id") Integer id) {
        this.attributeService.deleteAttribute(id);
        log.info("Attribute deleted");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "deleted"));
    }
}