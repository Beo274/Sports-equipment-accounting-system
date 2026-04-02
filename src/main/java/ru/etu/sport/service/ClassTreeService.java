package ru.etu.sport.service;

import java.util.List;

import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;

public interface ClassTreeService {
    List<ClassHierarchyResponse> getChildren(Integer classId);

    List<ClassHierarchyResponse> getParents(Integer classId);

    List<ClassResponse> getLeaves();

    void deleteProduct(Integer id);

    void deleteClass(Integer id);

    void updateClassMeasure(Integer classId, Integer measureId);
}
