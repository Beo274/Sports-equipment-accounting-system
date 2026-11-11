package ru.etu.sport.techroute.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.techroute.operations.persistence.WorkCenter;

public interface WorkCenterRepository extends JpaRepository<WorkCenter, Long> {

}
