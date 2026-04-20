package ru.etu.sport.category;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.category.projection.ClassHierarchyProjection;
import ru.etu.sport.repository.ClassRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassServiceImpl implements ClassService {
    private final ClassRepository classRepository;

    @Override
    public List<ClassHierarchyResponse> getChildren(Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("ClassId cannot be null");
        }

        List<ClassHierarchyProjection> projections = this.classRepository.findChildren(classId);

        log.info("Got children for classId: {}", classId);
        return projections.stream().map(this::convertToHierarchyResponse).collect(Collectors.toList());
    }

    @Override
    public List<ClassHierarchyResponse> getParents(Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("ClassId cannot be null");
        }

        List<ClassHierarchyProjection> projections = this.classRepository.findParents(classId);
        log.info("Got parents for classId: {}", classId);
        return projections.stream().map(this::convertToHierarchyResponse).collect(Collectors.toList());
    }

    @Override
    public List<ClassResponse> getLeaves() {
        List<ClassResponse> leaves = this.classRepository.findLeaves();
        log.info("Got leaves");
        return leaves;
    }

    @Override
    public void deleteClass(Integer id) {
        log.info("Service: deleting class {}", id);
        classRepository.deleteClass(id);
    }

    @Override
    public void updateClassMeasure(Integer classId, Integer measureId) {
        log.info("Service: updating measure for class {} to {}", classId, measureId);
        classRepository.updateClassMeasure(classId, measureId);
    }

    @Override
    public void swapBaseClass(Integer id, Integer parentId) {
        ClassEntity currentClass = this.classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));
        ClassEntity newParentClass = this.classRepository.findById(parentId)
                        .orElseThrow(() -> new EntityNotFoundException("Parent class not found"));
        currentClass.setBaseClassId(newParentClass);
    }

    @Override
    public Integer addClass(ClassEntity classEntity) {
        return classRepository.save(classEntity).getId();
    }

    private ClassHierarchyResponse convertToHierarchyResponse(ClassHierarchyProjection projection) {
        return ClassHierarchyResponse.builder()
            .id(projection.getId())
            .name(projection.getName())
            .shortName(projection.getShortName())
            .baseClassId(projection.getBaseClassId())
            .level(projection.getLevel())
            .mUnitId(projection.getMUnitId())
            .build();
    }
}
