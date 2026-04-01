package ru.etu.sport.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.etu.sport.model.entity.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
}

