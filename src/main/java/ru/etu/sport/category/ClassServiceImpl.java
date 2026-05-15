package ru.etu.sport.category;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.request.CreateClassDto;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.category.projection.ClassHierarchyProjection;
import ru.etu.sport.measure.MeasureRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassServiceImpl implements ClassService {
    private final ClassRepository classRepository;
    private final MeasureRepository measureRepository;

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
    @Transactional
    public void deleteClass(Integer id) {
        log.info("Service: deleting class {}", id);
        ClassEntity classToDelete = classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + id));
        ClassEntity parentClass = classToDelete.getBaseClass();
        if (parentClass != null) {
            List<ClassEntity> children = classRepository.findByBaseClassId(classToDelete);

            for (ClassEntity child : children) {
                child.setBaseClass(parentClass);
                classRepository.save(child);
            }
        }
        
        classRepository.deleteClass(id);
    }

    @Override
    @Transactional
    public void updateClassMeasure(Integer classId, Integer measureId) {
        log.info("Service: updating measure for class {} to {}", classId, measureId);
        classRepository.updateClassMeasure(classId, measureId);
    }

    @Override
    @Transactional
    public void swapBaseClass(Integer id, Integer parentId) {
        ClassEntity currentClass = this.classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));
        ClassEntity newParentClass = this.classRepository.getReferenceById(parentId);
        currentClass.setBaseClass(newParentClass);
    }

    @Override
    public Integer addClass(CreateClassDto createClassDto) {
        ClassEntity newClass =  new ClassEntity();
        newClass.setName(createClassDto.getName());
        newClass.setShortName(createClassDto.getShortName());

        if (createClassDto.getBaseClassId() != null) {
            ClassEntity baseClass = this.classRepository.getReferenceById(createClassDto.getBaseClassId());
            newClass.setBaseClass(baseClass);
        }

        if (createClassDto.getMeasureUnitId() != null) {
            Measure measure = this.measureRepository.getReferenceById(createClassDto.getMeasureUnitId());
            newClass.setMeasure(measure);
        }

        return classRepository.save(newClass).getId();
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

    public List<ClassResponse> getAll() {
        return this.classRepository.findAll().stream()
            .map(c -> ClassResponse.builder()
            .id(c.getId())
            .name(c.getName())
            .shortName(c.getShortName())
            .mUnitId(c.getMeasure() != null ? c.getMeasure().getId() : null)
            .baseClassId(c.getBaseClass() != null ? c.getBaseClass().getId() : null)
            .build())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteClassMeasure(Integer classId) {
        ClassEntity classEntity = this.classRepository.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("Class not found"));
        classEntity.setMeasure(null);
    }

    @Override
    @Transactional
    public void deleteBaseClass(Integer classId) {
        ClassEntity classEntity = this.classRepository.findById(classId)
            .orElseThrow(() -> new EntityNotFoundException("Class not found"));
        classEntity.setBaseClass(null);
    }
}