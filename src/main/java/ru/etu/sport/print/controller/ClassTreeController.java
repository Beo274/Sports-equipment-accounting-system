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
    
}
