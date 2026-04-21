package ru.etu.sport.attribute;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.dto.request.ValueDto;
import ru.etu.sport.model.entity.Attribute;
import ru.etu.sport.model.entity.Measure;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attribute")
public class AttributeController {

    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Attribute attribute) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attributeService.create(attribute));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteValue(@PathVariable Integer id) {
        attributeService.deleteValue(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateValue(@PathVariable Integer id, @RequestBody ValueDto valueDto) {
        valueDto.updateValue(attributeService, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAttributesByClassId(@RequestParam Integer classId) {
        return ResponseEntity.ok(attributeService.getAttributesByClassId(classId));
    }

    @PutMapping("/{id}/measure")
    public ResponseEntity<?> setMeasure(@PathVariable Integer id, @RequestBody Measure measure) {
        attributeService.setMeasure(id, measure);
        return ResponseEntity.ok().build();
    }
}