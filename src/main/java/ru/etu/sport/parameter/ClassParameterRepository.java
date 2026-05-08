package ru.etu.sport.parameter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.ClassParameter;
import java.util.List;

@Repository
public interface ClassParameterRepository extends JpaRepository<ClassParameter, Integer> {
}