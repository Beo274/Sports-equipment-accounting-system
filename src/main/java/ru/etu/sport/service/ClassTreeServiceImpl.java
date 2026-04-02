package ru.etu.sport.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.dao.ClassTreeDao;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClassTreeServiceImpl implements ClassTreeService {
    private final ClassTreeDao classTreeDao;

    @Override
    public List<ClassHierarchyResponse> getChildren(Integer classId) {
        log.info("Getting children for classId: {}", classId);

        if (classId == null) {
            throw new IllegalArgumentException("ClassId cannot be null");
        }

        List<ClassHierarchyResponse> children = this.classTreeDao.findChildren(classId);

        log.info("Got children for classId: {}", classId);
        return children;
    }

    @Override
    public List<ClassHierarchyResponse> getParents(Integer classId) {
        log.info("Getting parents for classId: {}", classId);

        if (classId == null) {
            throw new IllegalArgumentException("ClassId cannot be null");
        }

        List<ClassHierarchyResponse> parents = this.classTreeDao.findParents(classId);
        log.info("Got parents for classId: {}", classId);
        return parents;
    }

    @Override
    public List<ClassResponse> getLeaves() {
        log.info("Getting class leaves");
        
        List<ClassResponse> leaves = this.classTreeDao.findLeaves();
        log.info("Got leaves");
        return leaves;
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        log.info("Service: deleting product {}", id);
        classTreeDao.deleteProduct(id);
    }

    @Override
    @Transactional
    public void deleteClass(Integer id) {
        log.info("Service: deleting class {}", id);
        classTreeDao.deleteClass(id);
    }

    @Override
    @Transactional
    public void updateClassMeasure(Integer classId, Integer measureId) {
        log.info("Service: updating measure for class {} to {}", classId, measureId);
        classTreeDao.updateClassMeasure(classId, measureId);
    }
}
