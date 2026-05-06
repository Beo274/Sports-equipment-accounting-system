package ru.etu.sport.parameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.entity.Parameter;

@Controller
@RequestMapping("/param")
@Slf4j
public class ParameterController {
    private final ParameterService parameterService;

    public ParameterController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @PostMapping
    public ResponseEntity<?> createParam(@RequestBody Parameter parameter) {
        log.debug("Creating parameter: {}", parameter);
        Integer id = parameterService.create(parameter);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    };

//    @GetMapping
//    public ResponseEntity<> getParam(@PathVariable param_id) {
//
//
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateParam(@PathVariable Integer id, @RequestBody Parameter parameter) {
        parameterService.update(id, parameter);
        return ResponseEntity.status(HttpStatus.OK).body("updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParam(@PathVariable Integer id) {
        parameterService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted");
    }
}
