package ru.etu.sport.parameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.etu.sport.model.dto.request.CreateParamDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;
import ru.etu.sport.model.entity.Parameter;

@RestController
@RequestMapping("/param")
@Tag(name = "parameters", description = "Managing parameters")
@Slf4j
public class ParameterController {
    private final ParameterService parameterService;

    public ParameterController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @PostMapping
    public ResponseEntity<IdResponse> createParam(@RequestBody CreateParamDto createParamDto) {
        Integer id = parameterService.create(createParamDto);
        log.info("Parameter created with id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    };

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateParam(@PathVariable Integer id, @RequestBody CreateParamDto createParamDto) {
        parameterService.update(id, createParamDto);
        log.info("Parameter updated with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteParam(@PathVariable Integer id) {
        parameterService.delete(id);
        log.info("Parameter deleted with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("deleted"));
    }
}
