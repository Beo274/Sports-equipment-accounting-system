package ru.etu.sport.techroute.operations.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateOperation;

@Repository
public interface TechnologyProcessTemplateOperationRepository
        extends JpaRepository<TechnologyProcessTemplateOperation, Long> {

    List<TechnologyProcessTemplateOperation> findAllByTemplate_IdOrderByOperationNumberAsc(Long templateId);
}