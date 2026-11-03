package ru.etu.sport.operations.infrastructure.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.operations.application.ProductOperationService;
import ru.etu.sport.operations.dto.CreateProductOperationDto;
import ru.etu.sport.operations.dto.ProductOperationReponse;
import ru.etu.sport.operations.dto.UpdateProductOperationDto;
import ru.etu.sport.operations.infrastructure.repository.OperationTypeRepository;
import ru.etu.sport.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.operations.infrastructure.repository.ProfessionRepository;
import ru.etu.sport.operations.infrastructure.repository.QualificationRepository;
import ru.etu.sport.operations.infrastructure.repository.WorkCenterRepository;
import ru.etu.sport.operations.persistence.OperationType;
import ru.etu.sport.operations.persistence.ProductOperation;
import ru.etu.sport.operations.persistence.Profession;
import ru.etu.sport.operations.persistence.Qualification;
import ru.etu.sport.operations.persistence.WorkCenter;
import ru.etu.sport.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductOperationServiceImpl implements ProductOperationService {

    private final ProductOperationRepository productOperationRepository;
    private final ProductRepository productRepository;
    private final OperationTypeRepository operationTypeRepository;
    private final WorkCenterRepository workCenterRepository;
    private final QualificationRepository qualificationRepository;
    private final ProfessionRepository professionRepository;

    @Override
    public ProductOperationReponse create(CreateProductOperationDto dto) {
        Product product = this.productRepository
                .findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + dto.getProductId()));
        OperationType operationType = this.operationTypeRepository
                .findById(dto.getOperationTypeId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Operation type not found: " + dto.getOperationTypeId()));
        WorkCenter workCenter = this.workCenterRepository
                .findById(dto.getWorkCenterId())
                .orElseThrow(() -> new EntityNotFoundException("Work center not found: " + dto.getWorkCenterId()));
        Qualification qualification = this.qualificationRepository
                .findById(dto.getQualificationId())
                .orElseThrow(() -> new EntityNotFoundException("Qualification not found: " + dto.getQualificationId()));
        Profession profession = this.professionRepository
                .findById(dto.getProfessionId())
                .orElseThrow(() -> new EntityNotFoundException("Profession not found: " + dto.getProfessionId()));

        // Хочу проверить, есть ли операции тех маршрута с таким же порядковым номером
        List<ProductOperation> operations = productOperationRepository.findAllByProduct(product);
        Boolean isOperationNumberDuplicate = operations.stream()
                .anyMatch((op) -> op.getOperationNumber() == dto.getOperationNumber());
        if (isOperationNumberDuplicate) {
            throw new ValidationException(
                    "Product operation with such number already exists for provided product, number: "
                            + dto.getOperationNumber());
        }

        ProductOperation productOperation = ProductOperation.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .operationNumber(dto.getOperationNumber())
                .setupTime(dto.getSetupTime())
                .pieceTime(dto.getPieceTime())
                .product(product)
                .operationType(operationType)
                .workCenter(workCenter)
                .qualification(qualification)
                .profession(profession)
                .build();

        return ProductOperationReponse.fromEntity(this.productOperationRepository.save(productOperation));
    }

    @Override
    public void delete(Long id) {
        if (!this.productOperationRepository.existsById(id)) {
            throw new EntityNotFoundException("Product operation not found: " + id);
        }
        this.productOperationRepository.deleteById(id);
    }

    @Override
    public ProductOperationReponse getById(Long id) {
        ProductOperation productOperation = this.productOperationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product operation not found: " + id));
        return ProductOperationReponse.fromEntity(productOperation);
    }

    @Override
    public List<ProductOperationReponse> getByProductId(Long id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        List<ProductOperation> result = this.productOperationRepository.findAllByProduct(product);

        result.sort(Comparator.comparingLong(ProductOperation::getOperationNumber));

        return result.stream().map((po) -> ProductOperationReponse.fromEntity(po)).toList();
    }

    @Override
    @Transactional
    public ProductOperationReponse update(Long id, UpdateProductOperationDto dto) {
        ProductOperation productOperation = this.productOperationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product operation not found: " + id));

        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + dto.getProductId()));
            productOperation.setProduct(product);
        }

        if (dto.getOperationNumber() != null && dto.getOperationNumber() != productOperation.getOperationNumber()) {
            productOperation.setOperationNumber(dto.getOperationNumber());
        }

        if (dto.getOperationTypeId() != null) {
            OperationType operationType = operationTypeRepository.findById(dto.getOperationTypeId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Operation type not found: " + dto.getOperationTypeId()));
            productOperation.setOperationType(operationType);
        }

        if (dto.getWorkCenterId() != null) {
            WorkCenter workCenter = workCenterRepository.findById(dto.getWorkCenterId())
                    .orElseThrow(() -> new EntityNotFoundException("Work center not found: " + dto.getWorkCenterId()));
            productOperation.setWorkCenter(workCenter);
        }

        if (dto.getProfessionId() != null) {
            Profession profession = professionRepository.findById(dto.getProfessionId())
                    .orElseThrow(() -> new EntityNotFoundException("Profession not found: " + dto.getProfessionId()));
            productOperation.setProfession(profession);
        }

        if (dto.getQualificationId() != null) {
            Qualification qualification = qualificationRepository.findById(dto.getQualificationId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Qualification not found: " + dto.getQualificationId()));
            productOperation.setQualification(qualification);
        }

        if (dto.getName() != null) {
            productOperation.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            productOperation.setDescription(dto.getDescription());
        }

        if (dto.getSetupTime() != null) {
            productOperation.setSetupTime(dto.getSetupTime());
        }

        if (dto.getPieceTime() != null) {
            productOperation.setPieceTime(dto.getPieceTime());
        }

        return ProductOperationReponse.fromEntity(productOperation);
    }
}
