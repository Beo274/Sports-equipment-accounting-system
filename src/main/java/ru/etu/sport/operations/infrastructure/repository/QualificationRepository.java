package ru.etu.sport.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.operations.persistence.Qualification;

public interface QualificationRepository extends JpaRepository<Qualification, Long> {

}
