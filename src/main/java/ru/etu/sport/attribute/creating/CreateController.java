package ru.etu.sport.attribute.creating;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.entity.Attribute;

@RestController
@RequestMapping("/class")
public class CreateController {

    CreatingService creatingService;

    public CreateController(CreatingService creatingService) {
        this.creatingService = creatingService;
    }

    @PostMapping("/attribute")
    public ResponseEntity<?> create(@RequestBody Attribute attribute) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creatingService.create(attribute));
    }
}
