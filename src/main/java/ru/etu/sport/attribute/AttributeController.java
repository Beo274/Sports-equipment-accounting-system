package ru.etu.sport.attribute;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.dto.request.ValueDto;
import ru.etu.sport.model.entity.Attribute;

@RestController
@RequestMapping("/attribute")
public class AttributeController {

    AttributeService attributeService;

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

}
