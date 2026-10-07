package ru.etu.sport.measure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.etu.sport.model.dto.response.MessageResponse;
import ru.etu.sport.model.entity.Measure;
import org.springframework.web.bind.annotation.GetMapping;


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

    @GetMapping
    public ResponseEntity<List<Measure>> getMeasures() {
        log.info("Measure units provided");
        return ResponseEntity.ok().body(measureService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteMeasure(@PathVariable("id") Integer id) {
        this.measureService.deleteMeasure(id);
        log.info("Measure deleted");
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }
}