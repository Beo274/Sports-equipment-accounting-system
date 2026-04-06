package ru.etu.sport.service;

import java.util.List;

import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.model.entity.ClassEntity;

public interface ClassService {
    List<ClassHierarchyResponse> getChildren(Integer classId);

    List<ClassHierarchyResponse> getParents(Integer classId);

    List<ClassResponse> getLeaves();

    void deleteClass(Integer id);

    void updateClassMeasure(Integer classId, Integer measureId);

    void swapBaseClass(Integer id, Integer parentId);

    Integer addClass(ClassEntity classEntity);
}
