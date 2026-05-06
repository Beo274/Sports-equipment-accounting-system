package ru.etu.sport.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.Measure;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, Integer> {
}