package ru.etu.sport.category;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.request.CreateClassDto;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.model.dto.response.IdResponse;
import ru.etu.sport.model.dto.response.MessageResponse;
import ru.etu.sport.parameter.ClassProductParameterService;

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

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "classes", description = "Managing classes endpoints")
public class ClassController {
    private final ClassService classService;
    private final ClassProductParameterService classProductParameterService;

    @GetMapping("/{id}/children")
    public ResponseEntity<List<ClassHierarchyResponse>> getChildren(@PathVariable("id") Integer id) {
        List<ClassHierarchyResponse> children = this.classService.getChildren(id);
        log.info("Provided children");
        return ResponseEntity.ok(children);
    }

    @PostMapping
    public ResponseEntity<IdResponse> addClass(@Valid @RequestBody CreateClassDto createClassDto) {
        Integer id = classService.addClass(createClassDto);
        log.info("Class added");
        this.classProductParameterService.inheritParametersForClass(id);
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
    public ResponseEntity<MessageResponse> deleteClass(@PathVariable("id") Integer id) {
        classService.deleteClass(id);
        log.info("Class with id: {} deleted", id);
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @PutMapping("/{id}/measure")
    public ResponseEntity<MessageResponse> updateMeasure(@PathVariable("id") Integer id, @RequestParam Integer measureId) {
        classService.updateClassMeasure(id, measureId);
        log.info("Class measure unit updated");
        return ResponseEntity.ok(new MessageResponse("updated"));
    }

    @DeleteMapping("/{id}/measure")
    public ResponseEntity<MessageResponse> deleteMeasure(@PathVariable("id") Integer id) {
        classService.deleteClassMeasure(id);
        log.info("Class measure unit deleted");
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @PutMapping("/{id}/parent")
    public ResponseEntity<MessageResponse> updateBaseClass(@PathVariable("id") Integer id, @RequestParam("new") Integer parentId) {
        classService.swapBaseClass(id, parentId);
        log.info("Base class updated");
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("updated"));
    }

    @DeleteMapping("/{id}/parent")
    public ResponseEntity<MessageResponse> deleteBaseClass(@PathVariable("id") Integer id) {
        classService.deleteBaseClass(id);
        log.info("Base class deleted");
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @GetMapping
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        log.info("All classes provided");
        return ResponseEntity.ok().body(this.classService.getAll());
    }
    
}
