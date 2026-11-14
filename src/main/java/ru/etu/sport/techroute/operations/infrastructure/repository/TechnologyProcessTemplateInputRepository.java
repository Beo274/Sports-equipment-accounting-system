package ru.etu.sport.techroute.operations.infrastructure.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateInput;

@Repository
public interface TechnologyProcessTemplateInputRepository
        extends JpaRepository<TechnologyProcessTemplateInput, Long> {

    List<TechnologyProcessTemplateInput> findAllByOutputTemplateOperation_IdInOrderByIdAsc(
            Collection<Long> outputTemplateOperationIds
    );
}