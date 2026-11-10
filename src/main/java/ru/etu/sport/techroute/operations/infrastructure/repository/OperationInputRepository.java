package ru.etu.sport.techroute.operations.infrastructure.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.etu.sport.techroute.operations.persistence.OperationInput;
import ru.etu.sport.techroute.operations.persistence.ProductOperation;

public interface OperationInputRepository extends JpaRepository<OperationInput, Long> {
    List<OperationInput> findByOutputOperation_IdOrderByIdAsc(
        Long outputOperationId
    );

    Boolean existsByOutputOperation_IdAndInputOperation_Id(Long outputOperationId, Long inputOperationId);

    Optional<OperationInput> findByIdAndOutputOperation_Id(Long inputId, Long operationId);

    Boolean existsByOutputOperation_IdAndInputOperation_IdAndIdNot(Long operationId, Long inputOperationId, Long inputId);

    @Query("""
            SELECT oi.inputOperation
            FROM OperationInput oi
            WHERE oi.outputOperation = :operation
       """)
    LinkedList<ProductOperation> findInputOperationByOutputOperation(@Param("operation") ProductOperation operation);
    @Query("""
            SELECT oi.quantity
            FROM OperationInput oi
            WHERE oi.outputOperation = :operation
       """)
    Optional<Integer> findQuantityByOutputOperation(@Param("operation") ProductOperation op);

    @Query("""
            SELECT oi.baseQuantity
            FROM OperationInput oi
            WHERE oi.outputOperation = :operation
       """)
    Optional<Integer> findBaseQuantityByOutputOperation(@Param("operation") ProductOperation op);
}
