package ru.etu.sport.techroute.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.techroute.operations.persistence.Qualification;

public interface QualificationRepository extends JpaRepository<Qualification, Long> {

}
