package ru.etu.sport.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.service.ClassService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
@Slf4j
public class ClassController {
    private final ClassService classService;

    @GetMapping("/{id}/children")
    public ResponseEntity<List<ClassHierarchyResponse>> getChildren(@PathVariable("id") Integer id) {
        List<ClassHierarchyResponse> children = this.classService.getChildren(id);
        log.info("Provided children");
        return ResponseEntity.ok(children);
    }

    @PostMapping
    public ResponseEntity<IdResponse> addClass(@RequestBody ClassEntity classEntity) {
        Integer id = classService.addClass(classEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @GetMapping("/{id}/parents")
    public ResponseEntity<List<ClassHierarchyResponse>> getParents(@PathVariable("id") Integer id) {
        List<ClassHierarchyResponse> parents = this.classService.getParents(id);
        log.info("Provided parents");
        return ResponseEntity.ok(parents);
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<ClassResponse>> getLeaves() {
        List<ClassResponse> leaves = this.classService.getLeaves();
        log.info("provided leaves");
        return ResponseEntity.ok(leaves);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteClass(@PathVariable("id") Integer id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(Map.of("message", "deleted"));
    }

    @PutMapping("/{id}/measure")
    public ResponseEntity<Map<String, String>> updateMeasure(@PathVariable("id") Integer id, @RequestBody Map<String, Integer> payload) {
        Integer measureId = payload.get("measure_id");
        classService.updateClassMeasure(id, measureId);
        return ResponseEntity.ok(Map.of("message", "updated"));
    }

    @PutMapping("/{id}/swap")
    public ResponseEntity<Map<String, String>> updateClass(@PathVariable("id") Integer id, @RequestParam("new") Integer parentId) {
        classService.swapBaseClass(id, parentId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "update successful"));
    }
}
