package ru.etu.sport.parameter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.ClassParameter;

@Repository
public interface ClassParameterRepository extends JpaRepository<ClassParameter, Integer> {

    @Query("SELECT cp FROM ClassParameter cp " +
           "JOIN FETCH cp.parameter " +
           "JOIN FETCH cp.classEntity c " +
           "LEFT JOIN FETCH cp.enumerationValue ev " +
           "LEFT JOIN FETCH c.baseClass " +
           "WHERE cp.parameter.id IN :paramIds")
    List<ClassParameter> findByParamIds(@Param("paramIds") List<Integer> paramIds);

    @Query("SELECT cp FROM ClassParameter cp WHERE cp.classEntity.id = :classId")
    List<ClassParameter> findByClassId(@Param("classId") Integer classId);
    
    @Query("SELECT cp FROM ClassParameter cp " +
           "JOIN FETCH cp.parameter " +
           "WHERE cp.classEntity.id = :classId")
    List<ClassParameter> findByClassIdWithParam(@Param("classId") Integer classId);
    
    @Modifying
    @Query("DELETE FROM ClassParameter cp WHERE cp.classEntity.id = :classId")
    void deleteByClassId(@Param("classId") Integer classId);
}