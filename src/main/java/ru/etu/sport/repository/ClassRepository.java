package ru.etu.sport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.etu.sport.model.dto.response.ClassResponse;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.category.projection.ClassHierarchyProjection;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    @Query(value = "SELECT * FROM find_children(:classId)", nativeQuery = true)
    List<ClassHierarchyProjection> findChildren(@Param("classId") Integer classId);

    @Query(value = "SELECT * FROM find_parents(:classId)", nativeQuery = true)
    List<ClassHierarchyProjection> findParents(@Param("classId") Integer classId);
    
    @Query(value = "SELECT * FROM find_leaves()", nativeQuery = true)
    List<ClassResponse> findLeaves();

    @Modifying
    @Query("UPDATE ClassEntity c SET c.baseClassId = :newParentId WHERE c.id =:id")
    void updateParentId(@Param("id") Integer id, @Param("newParentId") Integer newParentId);

    @Modifying
    @Query("DELETE FROM ClassEntity c WHERE c.id = :id")
    void deleteClass(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE ClassEntity c SET c.mUnitId = :measureId WHERE c.id = :classId")
    void updateClassMeasure(@Param("classId") Integer classId, @Param("measureId") Integer measureId);
}

