package ru.etu.sport.parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.etu.sport.model.dto.request.ParamBindingDto;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;

import java.util.Map;

@RestController
@RequestMapping("/param/class")
@RequiredArgsConstructor
public class ClassParameterController {

    private final ClassProductParameterService service;

    @PostMapping
    public ResponseEntity<IdResponse> create(@RequestBody ParamBindingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(service.createClassParam(dto)));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of("items", service.getAllClassParams()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        service.deleteClassParam(id);
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@PathVariable Integer id, @RequestBody ParamBindingDto dto) {
        service.updateClassParam(id, dto);
        return ResponseEntity.ok(new MessageResponse("updated"));
    }
}