package ru.etu.sport.parameter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
}