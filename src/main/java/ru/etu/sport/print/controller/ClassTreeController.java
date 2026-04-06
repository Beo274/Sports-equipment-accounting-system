package ru.etu.sport.print.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.print.service.ClassTreeService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
@Slf4j
public class ClassTreeController {
    private final ClassTreeService classTreeService;

    @GetMapping("/{classId}/children")
    public ResponseEntity<List<ClassHierarchyResponse>> getChildren(@PathVariable Integer classId) {
        List<ClassHierarchyResponse> children = this.classTreeService.getChildren(classId);
        log.info("Provided children");
        return ResponseEntity.ok(children);
    }

    @GetMapping("/{classId}/parents")
    public ResponseEntity<List<ClassHierarchyResponse>> getParents(@PathVariable Integer classId) {
        List<ClassHierarchyResponse> parents = this.classTreeService.getParents(classId);
        log.info("Provided parents");
        return ResponseEntity.ok(parents);
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<ClassResponse>> getLeaves() {
        List<ClassResponse> leaves = this.classTreeService.getLeaves();
        log.info("provided leaves");
        return ResponseEntity.ok(leaves);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        classTreeService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "deleted"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Integer id) {
        classTreeService.deleteClass(id);
        return ResponseEntity.ok(Map.of("message", "deleted"));
    }

    @PutMapping("/{id}/measure")
    public ResponseEntity<?> updateMeasure(@PathVariable Integer id, @RequestBody Map<String, Integer> payload) {
        Integer measureId = payload.get("measure_id");
        classTreeService.updateClassMeasure(id, measureId);
        return ResponseEntity.ok(Map.of("message", "updated"));
    }

}
