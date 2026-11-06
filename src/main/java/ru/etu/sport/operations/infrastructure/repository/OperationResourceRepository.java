package ru.etu.sport.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.operations.persistence.OperationResource;

import java.util.Collection;
import java.util.List;

@Repository
public interface OperationResourceRepository extends JpaRepository<OperationResource, Long> {

    List<OperationResource> findAllByProductOperationId(Long productOperationId);

    List<OperationResource> findAllByProductOperationIdIn(Collection<Long> productOperationIds);
}