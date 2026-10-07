package ru.etu.sport.parameter;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.etu.sport.model.dto.request.CreateParamDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;
import ru.etu.sport.model.dto.response.ParameterGroupDto;
import ru.etu.sport.model.dto.response.ParameterResponse;


@RestController
@RequestMapping("/param")
@Tag(name = "parameters", description = "Managing parameters")
@Slf4j
public class ParameterController {
    private final ParameterService parameterService;
    private final ClassProductParameterService classProductParameterService;

    public ParameterController(ParameterService parameterService, ClassProductParameterService classProductParameterService) {
        this.parameterService = parameterService;
        this.classProductParameterService = classProductParameterService;
    }

    @PostMapping
    public ResponseEntity<IdResponse> createParam(@RequestBody CreateParamDto createParamDto) {
        Integer id = parameterService.create(createParamDto);
        log.info("Parameter created with id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    };

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateParam(@PathVariable Integer id, @RequestBody CreateParamDto createParamDto) {
        log.info("Parameter updated with id: {}", id);
        parameterService.update(id, createParamDto);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteParam(@PathVariable Integer id) {
        parameterService.delete(id);
        log.info("Parameter deleted with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("deleted"));
    }

    @GetMapping("/group")
    public ResponseEntity<List<ParameterGroupDto>> getParameterGroups(@RequestParam(required = false) Integer paramId) {
        List<ParameterGroupDto> groups = this.classProductParameterService.getParamsGroups();
        if (paramId != null) {
            groups = groups.stream().filter(parameter -> parameter.getId().equals(paramId)).toList();
        }
        log.info("Parameter groups provided");
        return ResponseEntity.ok(groups);
    }
    
    @GetMapping
    public ResponseEntity<List<ParameterResponse>> getAll() {
        log.info("List of parameters provided");
        return ResponseEntity.ok(this.parameterService.getAll());
    } 
}
