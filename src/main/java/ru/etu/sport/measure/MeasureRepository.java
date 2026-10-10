package ru.etu.sport.measure;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.etu.sport.model.entity.Measure;

public interface MeasureRepository extends JpaRepository<Measure, Integer> {
}