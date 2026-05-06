package ru.etu.sport.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.ClassEntity;
import ru.etu.sport.category.projection.ClassHierarchyProjection;
import ru.etu.sport.model.dto.response.ClassResponse;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM find_children(:id)")
    List<ClassHierarchyProjection> findChildren(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "SELECT * FROM find_parents(:id)")
    List<ClassHierarchyProjection> findParents(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "SELECT * FROM find_leaves()")
    List<ClassResponse> findLeaves();

    @Query(nativeQuery = true, value = "SELECT delete_class(:id)")
    void deleteClass(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "SELECT swap_base_class(:id, :newParent)")
    void swapBaseClass(@Param("id") Integer id, @Param("newParent") Integer newParent);

    @Query(nativeQuery = true, value = "UPDATE class SET m_unit_id = :measureId WHERE id = :classId")
    void updateClassMeasure(@Param("classId") Integer classId, @Param("measureId") Integer measureId);

    List<ClassEntity> findByBaseClassId(ClassEntity baseClassId);
}