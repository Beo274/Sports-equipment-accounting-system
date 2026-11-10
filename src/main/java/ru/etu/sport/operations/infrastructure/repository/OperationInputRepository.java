package ru.etu.sport.operations.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.operations.persistence.OperationInput;

public interface OperationInputRepository extends JpaRepository<OperationInput, Long> {
    List<OperationInput> findByOutputOperation_IdOrderByIdAsc(
        Long outputOperationId
    );

    Boolean existsByOutputOperation_IdAndInputOperation_Id(Long outputOperationId, Long inputOperationId);

    Optional<OperationInput> findByIdAndOutputOperation_Id(Long inputId, Long operationId);

    Boolean existsByOutputOperation_IdAndInputOperation_IdAndIdNot(Long operationId, Long inputOperationId, Long inputId);
}
