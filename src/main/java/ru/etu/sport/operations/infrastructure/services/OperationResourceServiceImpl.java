package ru.etu.sport.operations.infrastructure.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.operations.application.OperationResourceService;
import ru.etu.sport.operations.dto.OperationResourceDto;
import ru.etu.sport.operations.dto.OperationResourceResponse;
import ru.etu.sport.operations.dto.UpdateOperationResourceDto;
import ru.etu.sport.operations.infrastructure.repository.OperationResourceRepository;
import ru.etu.sport.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.operations.persistence.OperationResource;
import ru.etu.sport.operations.persistence.ProductOperation;
import ru.etu.sport.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class OperationResourceServiceImpl implements OperationResourceService {

    private final OperationResourceRepository operationResourceRepository;
    private final ProductOperationRepository productOperationRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public OperationResourceResponse create(OperationResourceDto dto) {
        if (dto.quantity() != null && dto.quantity() <= 0) {
            throw new ValidationException("Quantity must be greater than 0");
        }
        if (dto.baseQuantity() != null && dto.baseQuantity() <= 0) {
            throw new ValidationException("Base quantity must be greater than 0");
        }

        ProductOperation operation = productOperationRepository.findById(dto.productOperationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product operation not found: " + dto.productOperationId()));

        Product resource = productRepository.findById(dto.resourceId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Resource product not found: " + dto.resourceId()));

        Measure unit = Optional.ofNullable(
                        dto.unitId() != null ? entityManager.find(Measure.class, dto.unitId().intValue()) : null)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Measure unit not found: " + dto.unitId()));

        OperationResource entity = OperationResource.builder()
                .productOperation(operation)
                .resource(resource)
                .resourceType(dto.resourceType())
                .quantity(dto.quantity() != null ? dto.quantity() : 1L)
                .baseQuantity(dto.baseQuantity() != null ? dto.baseQuantity() : 1L)
                .unit(unit)
                .build();

        return OperationResourceResponse.fromEntity(operationResourceRepository.save(entity));
    }

    @Override
    @Transactional
    public OperationResourceResponse update(Long id, UpdateOperationResourceDto dto) {
        OperationResource entity = operationResourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Operation resource not found: " + id));

        if (dto.productOperationId() != null) {
            ProductOperation operation = productOperationRepository.findById(dto.productOperationId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product operation not found: " + dto.productOperationId()));
            entity.setProductOperation(operation);
        }

        if (dto.resourceId() != null) {
            Product resource = productRepository.findById(dto.resourceId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Resource product not found: " + dto.resourceId()));
            entity.setResource(resource);
        }

        if (dto.resourceType() != null) {
            entity.setResourceType(dto.resourceType());
        }

        if (dto.quantity() != null) {
            if (dto.quantity() <= 0) {
                throw new ValidationException("Quantity must be greater than 0");
            }
            entity.setQuantity(dto.quantity());
        }

        if (dto.baseQuantity() != null) {
            if (dto.baseQuantity() <= 0) {
                throw new ValidationException("Base quantity must be greater than 0");
            }
            entity.setBaseQuantity(dto.baseQuantity());
        }

        if (dto.unitId() != null) {
            Measure unit = Optional.ofNullable(entityManager.find(Measure.class, dto.unitId().intValue()))
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Measure unit not found: " + dto.unitId()));
            entity.setUnit(unit);
        }

        return OperationResourceResponse.fromEntity(operationResourceRepository.save(entity));
    }

    @Override
    public OperationResourceResponse getById(Long id) {
        OperationResource entity = operationResourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Operation resource not found: " + id));
        return OperationResourceResponse.fromEntity(entity);
    }

    @Override
    public List<OperationResourceResponse> getByOperationId(Long operationId) {
        if (!productOperationRepository.existsById(operationId)) {
            throw new EntityNotFoundException("Product operation not found: " + operationId);
        }
        return operationResourceRepository.findAllByProductOperationId(operationId)
                .stream()
                .map(OperationResourceResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!operationResourceRepository.existsById(id)) {
            throw new EntityNotFoundException("Operation resource not found: " + id);
        }
        operationResourceRepository.deleteById(id);
    }
}