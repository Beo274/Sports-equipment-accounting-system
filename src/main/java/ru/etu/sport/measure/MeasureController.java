package ru.etu.sport.measure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.etu.sport.model.entity.Measure;

@RestController
@RequestMapping("/measure")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "measure units", description = "Endpoints to manage measure units")
public class MeasureController {
    private final MeasureService measureService;

    @PutMapping
    public ResponseEntity<Measure> createMeasure(@RequestBody Measure measure) {
        log.info("Measure unit created");
        return ResponseEntity.status(HttpStatus.CREATED).body(measureService.createMeasure(measure));
    }
}