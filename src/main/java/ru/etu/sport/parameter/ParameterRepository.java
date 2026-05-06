package ru.etu.sport.parameter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {
}
