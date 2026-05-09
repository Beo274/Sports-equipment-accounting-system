package ru.etu.sport.parameter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {

    @Query("SELECT p FROM Parameter p LEFT JOIN FETCH p.measure")
    List<Parameter> findAllWithMeasure();
}
