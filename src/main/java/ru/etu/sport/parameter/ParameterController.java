package ru.etu.sport.parameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;
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
        Integer id = parameterService.create(parameter);
        log.info("Parameter created with id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    };

//    @GetMapping
//    public ResponseEntity<> getParam(@PathVariable param_id) {
//
//
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateParam(@PathVariable Integer id, @RequestBody Parameter parameter) {
        parameterService.update(id, parameter);
        log.info("Parameter updated with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParam(@PathVariable Integer id) {
        parameterService.delete(id);
        log.info("Parameter deleted with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("deleted"));
    }
}
