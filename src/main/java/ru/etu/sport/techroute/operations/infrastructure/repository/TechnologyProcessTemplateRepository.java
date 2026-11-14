package ru.etu.sport.techroute.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplate;

@Repository
public interface TechnologyProcessTemplateRepository extends JpaRepository<TechnologyProcessTemplate, Long> {
}