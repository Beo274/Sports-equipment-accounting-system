package ru.etu.sport.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.operations.persistence.WorkCenter;

public interface WorkCenterRepository extends JpaRepository<WorkCenter, Long> {

}
