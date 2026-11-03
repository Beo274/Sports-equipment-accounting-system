package ru.etu.sport.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.operations.persistence.OperationType;

public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {

}
