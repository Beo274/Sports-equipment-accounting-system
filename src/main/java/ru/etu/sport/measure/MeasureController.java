package ru.etu.sport.measure;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.entity.Measure;

@RestController
@RequestMapping("/measure")
@RequiredArgsConstructor
public class MeasureController {
    private final MeasureService measureService;

    @PutMapping
    public ResponseEntity<Measure> createMeasure(@RequestBody Measure measure) {
        return ResponseEntity.status(HttpStatus.CREATED).body(measureService.createMeasure(measure));
    }
}