package ru.etu.sport.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.service.ClassTreeService;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
@Slf4j
public class ClassTreeController {
    private final ClassTreeService classTreeService;

    @GetMapping("/{classId}/children")
    public ResponseEntity<Map<String, List<ClassHierarchyResponse>>> getChildren(@PathVariable Integer classId) {
        List<ClassHierarchyResponse> children = this.classTreeService.getChildren(classId);
        log.info("Provided children for classId: {}", classId);
        return ResponseEntity.ok(Map.of("classes", children));
    }

    @GetMapping("/{classId}/parents")
    public ResponseEntity<Map<String, List<ClassHierarchyResponse>>> getParents(@PathVariable Integer classId) {
        List<ClassHierarchyResponse> parents = this.classTreeService.getParents(classId);
        log.info("Provided parents for classId: {}", classId);
        return ResponseEntity.ok(Map.of("classes", parents));
    }

    @GetMapping("/leaves")
    public ResponseEntity<Map<String, List<ClassResponse>>> getLeaves() {
        List<ClassResponse> leaves = this.classTreeService.getLeaves();
        log.info("Provided leaves");
        return ResponseEntity.ok(Map.of("classes", leaves));
    }
    
}
