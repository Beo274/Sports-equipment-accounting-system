package ru.etu.sport.techroute.operations.infrastructure.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;
import ru.etu.sport.techroute.operations.application.TechnologyProcessTemplateService;
import ru.etu.sport.techroute.operations.dto.CreateTechnologyProcessTemplateDto;
import ru.etu.sport.techroute.operations.dto.ProductOperationReponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateInputResponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateOperationResponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateResponse;
import ru.etu.sport.techroute.operations.dto.TechnologyProcessTemplateShortResponse;
import ru.etu.sport.techroute.operations.infrastructure.repository.OperationInputRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.TechnologyProcessTemplateInputRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.TechnologyProcessTemplateOperationRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.TechnologyProcessTemplateRepository;
import ru.etu.sport.techroute.operations.persistence.OperationInput;
import ru.etu.sport.techroute.operations.persistence.ProductOperation;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplate;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateInput;
import ru.etu.sport.techroute.operations.persistence.TechnologyProcessTemplateOperation;

@Service
@RequiredArgsConstructor
public class TechnologyProcessTemplateServiceImpl implements TechnologyProcessTemplateService {

    private final TechnologyProcessTemplateRepository templateRepository;
    private final TechnologyProcessTemplateOperationRepository templateOperationRepository;
    private final TechnologyProcessTemplateInputRepository templateInputRepository;
    private final ProductRepository productRepository;
    private final ProductOperationRepository productOperationRepository;
    private final OperationInputRepository operationInputRepository;

    @Override
    @Transactional
    public TechnologyProcessTemplateResponse createFromProduct(
            Long productId,
            CreateTechnologyProcessTemplateDto dto
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        List<ProductOperation> productOperations = new ArrayList<>(
                productOperationRepository.findAllByProduct(product)
        );
        productOperations.sort(Comparator.comparingLong(ProductOperation::getOperationNumber));

        if (productOperations.isEmpty()) {
            throw new ValidationException("Product has no operations to create template from: " + productId);
        }

        String templateName = (dto != null && dto.name() != null && !dto.name().isBlank())
                ? dto.name()
                : "Изготовление: " + product.getName();

        String templateDescription = (dto != null && dto.description() != null)
                ? dto.description()
                : "Типовой процесс на основе изделия " + product.getName();

        TechnologyProcessTemplate template = TechnologyProcessTemplate.builder()
                .name(templateName)
                .description(templateDescription)
                .build();
        template = templateRepository.save(template);

        Map<Long, TechnologyProcessTemplateOperation> opMapping = new HashMap<>();
        List<TechnologyProcessTemplateOperation> savedTemplateOps = new ArrayList<>();

        for (ProductOperation prodOp : productOperations) {
            TechnologyProcessTemplateOperation templateOp = TechnologyProcessTemplateOperation.builder()
                    .template(template)
                    .name(prodOp.getName())
                    .description(prodOp.getDescription() != null ? prodOp.getDescription() : "")
                    .kind(prodOp.getKind())
                    .operationNumber(prodOp.getOperationNumber() != null ? prodOp.getOperationNumber().intValue() : 1)
                    .setupTime(prodOp.getSetupTime() != null ? prodOp.getSetupTime() : 0L)
                    .pieceTime(prodOp.getPieceTime() != null ? prodOp.getPieceTime() : 0L)
                    .operationType(prodOp.getOperationType())
                    .workCenter(prodOp.getWorkCenter())
                    .profession(prodOp.getProfession())
                    .qualification(prodOp.getQualification())
                    .build();

            templateOp = templateOperationRepository.save(templateOp);
            opMapping.put(prodOp.getId(), templateOp);
            savedTemplateOps.add(templateOp);
        }

        for (ProductOperation prodOp : productOperations) {
            List<OperationInput> inputs = operationInputRepository.findByOutputOperation_IdOrderByIdAsc(prodOp.getId());
            TechnologyProcessTemplateOperation outTemplateOp = opMapping.get(prodOp.getId());

            for (OperationInput input : inputs) {
                TechnologyProcessTemplateOperation inTemplateOp =
                        opMapping.get(input.getInputOperation().getId());

                // Если входная операция также принадлежит этому изделию — связываем внутри шаблона.
                // Если это операция внешнего полуфабриката, дополнительно клонируем или сохраняем ссылку.
                if (inTemplateOp == null) {
                    ProductOperation extOp = input.getInputOperation();
                    inTemplateOp = TechnologyProcessTemplateOperation.builder()
                            .template(template)
                            .name(extOp.getName())
                            .description(extOp.getDescription() != null ? extOp.getDescription() : "")
                            .kind(extOp.getKind())
                            .operationNumber(extOp.getOperationNumber() != null ? extOp.getOperationNumber().intValue() : 1)
                            .setupTime(extOp.getSetupTime() != null ? extOp.getSetupTime() : 0L)
                            .pieceTime(extOp.getPieceTime() != null ? extOp.getPieceTime() : 0L)
                            .operationType(extOp.getOperationType())
                            .workCenter(extOp.getWorkCenter())
                            .profession(extOp.getProfession())
                            .qualification(extOp.getQualification())
                            .build();
                    inTemplateOp = templateOperationRepository.save(inTemplateOp);
                    opMapping.put(extOp.getId(), inTemplateOp);
                    savedTemplateOps.add(inTemplateOp);
                }

                TechnologyProcessTemplateInput templateInput = TechnologyProcessTemplateInput.builder()
                        .outputTemplateOperation(outTemplateOp)
                        .inputTemplateOperation(inTemplateOp)
                        .externalInputOperation(
                                input.getInputOperation().getProduct().getId().equals(product.getId())
                                        ? null
                                        : input.getInputOperation()
                        )
                        .quantity(input.getQuantity())
                        .baseQuantity(input.getBaseQuantity())
                        .measure(input.getMeasure())
                        .build();

                templateInputRepository.save(templateInput);
            }
        }

        return buildTemplateResponse(template, savedTemplateOps);
    }

    @Override
    public List<TechnologyProcessTemplateShortResponse> getAll() {
        return templateRepository.findAll()
                .stream()
                .map(TechnologyProcessTemplateShortResponse::fromEntity)
                .toList();
    }

    @Override
    public TechnologyProcessTemplateResponse getById(Long templateId) {
        TechnologyProcessTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Technology process template not found: " + templateId));

        List<TechnologyProcessTemplateOperation> operations =
                templateOperationRepository.findAllByTemplate_IdOrderByOperationNumberAsc(templateId);

        return buildTemplateResponse(template, operations);
    }

    @Override
    @Transactional
    public TechnologyProcessTemplateResponse update(Long templateId, CreateTechnologyProcessTemplateDto dto) {
        TechnologyProcessTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Technology process template not found: " + templateId));

        if (dto.name() != null && !dto.name().isBlank()) {
            template.setName(dto.name());
        }
        if (dto.description() != null) {
            template.setDescription(dto.description());
        }

        template = templateRepository.save(template);
        List<TechnologyProcessTemplateOperation> operations =
                templateOperationRepository.findAllByTemplate_IdOrderByOperationNumberAsc(templateId);

        return buildTemplateResponse(template, operations);
    }

    @Override
    @Transactional
    public void delete(Long templateId) {
        if (!templateRepository.existsById(templateId)) {
            throw new EntityNotFoundException("Technology process template not found: " + templateId);
        }
        templateRepository.deleteById(templateId);
    }

    @Override
    @Transactional
    public List<ProductOperationReponse> applyToProduct(Long productId, Long templateId) {
        Product targetProduct = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        TechnologyProcessTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Technology process template not found: " + templateId));

        List<TechnologyProcessTemplateOperation> templateOperations =
                templateOperationRepository.findAllByTemplate_IdOrderByOperationNumberAsc(template.getId());

        if (templateOperations.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> templateOpIds = templateOperations.stream()
                .map(TechnologyProcessTemplateOperation::getId)
                .toList();

        List<TechnologyProcessTemplateInput> templateInputs =
                templateInputRepository.findAllByOutputTemplateOperation_IdInOrderByIdAsc(templateOpIds);

        Map<Long, ProductOperation> createdOpMap = new HashMap<>();
        List<ProductOperation> createdOperations = new ArrayList<>();

        // Шаг 1–3: создаём новые записи ProductOperation для целевого изделия
        for (TechnologyProcessTemplateOperation templateOp : templateOperations) {
            ProductOperation newProdOp = ProductOperation.builder()
                    .product(targetProduct)
                    .operationNumber(templateOp.getOperationNumber() != null
                            ? templateOp.getOperationNumber().longValue()
                            : 1L)
                    .name(templateOp.getName())
                    .description(templateOp.getDescription() != null ? templateOp.getDescription() : "")
                    .kind(templateOp.getKind())
                    .setupTime(templateOp.getSetupTime() != null ? templateOp.getSetupTime() : 0L)
                    .pieceTime(templateOp.getPieceTime() != null ? templateOp.getPieceTime() : 0L)
                    .operationType(templateOp.getOperationType())
                    .workCenter(templateOp.getWorkCenter())
                    .profession(templateOp.getProfession())
                    .qualification(templateOp.getQualification())
                    .build();

            newProdOp = productOperationRepository.save(newProdOp);
            createdOpMap.put(templateOp.getId(), newProdOp);
            createdOperations.add(newProdOp);
        }

        // Шаг 4–5: создаём новые записи OperationInput, связывая созданные ProductOperation
        Measure defaultMeasure = targetProduct.getProductClass() != null
                ? null
                : null;

        for (TechnologyProcessTemplateInput tInput : templateInputs) {
            ProductOperation outOp = createdOpMap.get(tInput.getOutputTemplateOperation().getId());
            ProductOperation inOp = tInput.getInputTemplateOperation() != null
                    ? createdOpMap.get(tInput.getInputTemplateOperation().getId())
                    : tInput.getExternalInputOperation();

            if (outOp != null && inOp != null && tInput.getMeasure() != null) {
                OperationInput newInput = OperationInput.builder()
                        .outputOperation(outOp)
                        .inputOperation(inOp)
                        .quantity(tInput.getQuantity())
                        .baseQuantity(tInput.getBaseQuantity())
                        .measure(tInput.getMeasure())
                        .build();

                operationInputRepository.save(newInput);
            }
        }

        return createdOperations.stream()
                .map(ProductOperationReponse::fromEntity)
                .toList();
    }

    private TechnologyProcessTemplateResponse buildTemplateResponse(
            TechnologyProcessTemplate template,
            List<TechnologyProcessTemplateOperation> operations
    ) {
        List<Long> opIds = operations.stream()
                .map(TechnologyProcessTemplateOperation::getId)
                .toList();

        Map<Long, List<TechnologyProcessTemplateInputResponse>> inputsByOutOpId = opIds.isEmpty()
                ? Collections.emptyMap()
                : templateInputRepository.findAllByOutputTemplateOperation_IdInOrderByIdAsc(opIds)
                .stream()
                .map(TechnologyProcessTemplateInputResponse::fromEntity)
                .collect(Collectors.groupingBy(
                        TechnologyProcessTemplateInputResponse::outputTemplateOperationId
                ));

        List<TechnologyProcessTemplateOperationResponse> opResponses = operations.stream()
                .map(op -> TechnologyProcessTemplateOperationResponse.fromEntity(
                        op,
                        inputsByOutOpId.getOrDefault(op.getId(), Collections.emptyList())
                ))
                .toList();

        return TechnologyProcessTemplateResponse.fromEntity(template, opResponses);
    }
}