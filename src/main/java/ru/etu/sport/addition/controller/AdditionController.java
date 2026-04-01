package ru.etu.sport.addition.controller;

import com.sun.source.tree.ClassTree;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.etu.sport.addition.service.AdditionService;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Product;

@RestController
public class AdditionController {
    AdditionService additionService;

    public AdditionController(AdditionService additionService) {
        this.additionService = additionService;
    }

    @PostMapping("/class")
    public ResponseEntity<?> addClass(@RequestBody ClassEntity classEntity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(additionService.addClass(classEntity));
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(additionService.addProduct(product));
    }
}
