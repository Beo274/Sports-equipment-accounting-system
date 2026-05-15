package ru.etu.sport.category;

import java.util.List;

import ru.etu.sport.model.dto.request.CreateClassDto;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;

public interface ClassService {
    List<ClassHierarchyResponse> getChildren(Integer classId);

    List<ClassHierarchyResponse> getParents(Integer classId);

    List<ClassResponse> getLeaves();

    void deleteClass(Integer id);

    void updateClassMeasure(Integer classId, Integer measureId);

    void deleteClassMeasure(Integer classId);

    void swapBaseClass(Integer id, Integer parentId);

    void deleteBaseClass(Integer classId);

    Integer addClass(CreateClassDto classEntity);

    List<ClassResponse> getAll();
}
