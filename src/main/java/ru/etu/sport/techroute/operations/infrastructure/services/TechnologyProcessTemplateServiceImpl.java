package ru.etu.sport.techroute.operations.infrastructure.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.measure.dto.MeasureResponse;
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

        List<ProductOperation> rootOperations = new ArrayList<>(
            productOperationRepository.findAllByProduct(product)
        );

        rootOperations.sort(
            Comparator.comparing(
                ProductOperation::getOperationNumber,
                Comparator.nullsLast(Comparator.naturalOrder())
            )
        );

        if (rootOperations.isEmpty()) {
            throw new ValidationException(
                "Product has no operations to create template from: " + productId
            );
        }

        String templateName = (dto != null && dto.name() != null && !dto.name().isBlank())
                ? dto.name()
                : "Изготовление: " + product.getName();

        String templateDescription = (dto != null && dto.description() != null)
                ? dto.description()
                : "Типовой процесс на основе изделия " + product.getName();

        TechnologyProcessTemplate template = templateRepository.save(TechnologyProcessTemplate.builder()
                .name(templateName)
                .description(templateDescription)
                .build()
        );

        // все достижимые операции (по инпутам тоже)
        Map<Long, ProductOperation> sourceOperations = collectAllOperations(rootOperations);

        Map<Long, TechnologyProcessTemplateOperation> operationMapping = new LinkedHashMap<>();
        List<TechnologyProcessTemplateOperation> savedTemplateOperations = new ArrayList<>();

        // добавляем все операции (пока без связей)
        for (ProductOperation sourceOperation : sourceOperations.values()) {
            TechnologyProcessTemplateOperation templateOperation = TechnologyProcessTemplateOperation.builder()
                .template(template)
                .name(sourceOperation.getName())
                .description(sourceOperation.getDescription() != null ? sourceOperation.getDescription() : "")
                .kind(sourceOperation.getKind())
                .operationNumber(sourceOperation.getOperationNumber() != null ? sourceOperation.getOperationNumber() : 1L)
                .setupTime(sourceOperation.getSetupTime() != null ? sourceOperation.getSetupTime() : 0L)
                .pieceTime(sourceOperation.getPieceTime() != null ? sourceOperation.getPieceTime() : 0L)
                .operationType(sourceOperation.getOperationType())
                .workCenter(sourceOperation.getWorkCenter())
                .profession(sourceOperation.getProfession())
                .qualification(sourceOperation.getQualification())
                .build();

            templateOperation = templateOperationRepository.save(templateOperation);

            operationMapping.put(sourceOperation.getId(), templateOperation);

            savedTemplateOperations.add(templateOperation);
        }

        // добавление связей
        for (ProductOperation sourceOperation : sourceOperations.values()) {
            TechnologyProcessTemplateOperation outputTemplateOperation = operationMapping.get(sourceOperation.getId());

            List<OperationInput> sourceInputs = operationInputRepository.findByOutputOperation_IdOrderByIdAsc(sourceOperation.getId());

            for (OperationInput sourceInput : sourceInputs) {
                ProductOperation sourceInputOperation = sourceInput.getInputOperation();

                if (sourceInputOperation == null) {
                    throw new ValidationException(
                        "Input operation is null for OperationInput: "
                                + sourceInput.getId()
                    );
                }

                TechnologyProcessTemplateOperation inputTemplateOperation = operationMapping.get(sourceInputOperation.getId());

                if (inputTemplateOperation == null) {
                    throw new IllegalStateException(
                         "Operation was not copied to template. Source operation ID: "
                                + sourceInputOperation.getId()
                    );
                }

                TechnologyProcessTemplateInput templateInput = TechnologyProcessTemplateInput.builder()
                    .outputTemplateOperation(outputTemplateOperation)
                    .inputTemplateOperation(inputTemplateOperation)
                    .externalInputOperation(sourceInputOperation)
                    .quantity(sourceInput.getQuantity())
                    .baseQuantity(sourceInput.getBaseQuantity())
                    .measure(sourceInput.getMeasure())
                    .build();
                
                templateInputRepository.save(templateInput);
            }
        }

        return buildTemplateResponse(template, savedTemplateOperations);
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

        Set<Long> inputOperationIds = templateInputs.stream()
            .map(TechnologyProcessTemplateInput::getInputTemplateOperation)
            .filter(Objects::nonNull)
            .map(TechnologyProcessTemplateOperation::getId)
            .collect(Collectors.toSet());

        Map<Long, Product> productMapping = new HashMap<>();

        // маппинг операций к изделиям
        for (TechnologyProcessTemplateOperation templateOperation : templateOperations) {
            Long templateOperationId = templateOperation.getId();

            // Если корневоая операция - меняем изделие
            if (!inputOperationIds.contains(templateOperationId)) {
                productMapping.put(templateOperationId, targetProduct);
                continue;
            }

            List<TechnologyProcessTemplateInput> matchingInputs = templateInputs.stream()
                .filter(input -> 
                    input.getInputTemplateOperation() != null 
                    && input.getInputTemplateOperation().getId().equals(templateOperationId)
                )
                .toList();
            
            if (matchingInputs.isEmpty()) {
                throw new IllegalStateException(
                    "Cannot determine source Product for template operation: " + templateOperationId
                );
            }

            Product sourceProduct = null;

            for (TechnologyProcessTemplateInput input : matchingInputs) {
                ProductOperation externalOperation = input.getExternalInputOperation();

                if (externalOperation == null || externalOperation.getProduct() == null) {
                    throw new IllegalStateException(
                        "External source operation or its product is missing: input ID: " + input.getId()
                    );
                }

                Product currentSourceProduct = externalOperation.getProduct();
                if (sourceProduct == null) {
                    sourceProduct = currentSourceProduct;
                } else if (!sourceProduct.getId().equals(currentSourceProduct.getId())) {
                    throw new IllegalStateException(
                        "Conflicting source Products for template operations: " + templateOperationId
                    );
                }
            }
            
            productMapping.put(templateOperationId, sourceProduct);
        }

        Map<Long, ProductOperation> createdOpMap = new LinkedHashMap<>();

        List<ProductOperation> createdOperations = new ArrayList<>();

        // создание операций
        for (TechnologyProcessTemplateOperation templateOperation : templateOperations) {
            Product operationProduct = productMapping.get(templateOperation.getId());

            if (operationProduct == null) {
                throw new IllegalStateException(
                    "Product mapping not found for template operation: "
                         + templateOperation.getId()
                );
            }

            ProductOperation newProductOperation = ProductOperation.builder()
                .product(operationProduct)
                .operationNumber(
                    templateOperation.getOperationNumber() != null
                            ? templateOperation.getOperationNumber().longValue()
                            : 1L
                )
                .name(templateOperation.getName())
                .description(
                        templateOperation.getDescription() != null
                                ? templateOperation.getDescription()
                                : ""
                )
                .kind(templateOperation.getKind())
                .setupTime(
                        templateOperation.getSetupTime() != null
                                ? templateOperation.getSetupTime()
                                : 0L
                )
                .pieceTime(
                        templateOperation.getPieceTime() != null
                                ? templateOperation.getPieceTime()
                                : 0L
                )
                .operationType(templateOperation.getOperationType())
                .workCenter(templateOperation.getWorkCenter())
                .profession(templateOperation.getProfession())
                .qualification(templateOperation.getQualification())
                .build();

            newProductOperation = productOperationRepository.save(newProductOperation);

            createdOpMap.put(templateOperation.getId(), newProductOperation);
            createdOperations.add(newProductOperation);
        }

        // создание OperationInput
        for (TechnologyProcessTemplateInput templateInput : templateInputs) {
            ProductOperation outputOperation = createdOpMap.get(templateInput.getOutputTemplateOperation().getId());

            ProductOperation inputOperation;

            if (templateInput.getInputTemplateOperation() != null) {
                inputOperation = createdOpMap.get(templateInput.getInputTemplateOperation().getId());
            } else {
                inputOperation = templateInput.getExternalInputOperation();
            }

            if (outputOperation == null || inputOperation == null) {
                throw new IllegalStateException("Cannot restore OperationInput. Template input: " + templateInput.getId());
            }

            OperationInput newInput = OperationInput.builder()
                .outputOperation(outputOperation)
                .inputOperation(inputOperation)
                .quantity(templateInput.getQuantity())
                .baseQuantity(templateInput.getBaseQuantity())
                .measure(templateInput.getMeasure())
                .build();

            operationInputRepository.save(newInput); 
        }

        return createdOperations.stream()
            .map(ProductOperationReponse::fromEntity)
            .toList();
    }

    private TechnologyProcessTemplateResponse buildTemplateResponse(
            TechnologyProcessTemplate template,
            List<TechnologyProcessTemplateOperation> operations
    ) {
        if (operations.isEmpty()) {
            return TechnologyProcessTemplateResponse.fromEntity(template, Collections.emptyList());
        }

        List<Long> operationsIds = operations.stream()
            .map(TechnologyProcessTemplateOperation::getId)
            .toList();
        
        List<TechnologyProcessTemplateInput> allInputs = 
            templateInputRepository.findAllByOutputTemplateOperation_IdInOrderByIdAsc(operationsIds);

        Map<Long, List<TechnologyProcessTemplateInput>> inputsByOutputOperarion = 
            allInputs.stream()
                .collect(Collectors.groupingBy(
                    input -> input.getOutputTemplateOperation().getId(),
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
        
        Set<Long> inputOperationIds = allInputs.stream()
            .map(TechnologyProcessTemplateInput::getInputTemplateOperation)
            .filter(Objects::nonNull)
            .map(TechnologyProcessTemplateOperation::getId)
            .collect(Collectors.toSet());
        
        List<TechnologyProcessTemplateOperation> rootOperations = operations.stream()
                .filter(operation -> !inputOperationIds.contains(operation.getId()))
                .sorted(
                    Comparator.comparing(
                        TechnologyProcessTemplateOperation::getOperationNumber,
                        Comparator.nullsLast(Comparator.naturalOrder())
                    ).thenComparing(
                        TechnologyProcessTemplateOperation::getId
                    )
                )
                .toList();
        
        if (rootOperations.isEmpty()) {
            throw new IllegalStateException(
                "Technology process template has no root operations: " + template.getId()
            );
        }

        List<TechnologyProcessTemplateOperationResponse> operationResponses = 
            rootOperations.stream()
                .map(operation -> buildOperationGraph(
                    operation,
                    inputsByOutputOperarion,
                    new HashSet<>()
                ))
                .toList();
        return TechnologyProcessTemplateResponse.fromEntity(template, operationResponses);
    }

    private Map<Long, ProductOperation> collectAllOperations(List<ProductOperation> rootOperations) {
        Map<Long, ProductOperation> result = new LinkedHashMap<>();

        Queue<ProductOperation> queue = new ArrayDeque<>(rootOperations);

        while (!queue.isEmpty()) {
            ProductOperation current = queue.poll();

            if (current == null) {
                continue;
            }

            Long operationId = current.getId();
            
            if (result.containsKey(operationId)) {
                continue;
            }

            result.put(operationId, current);

            List<OperationInput> inputs = operationInputRepository.findByOutputOperation_IdOrderByIdAsc(operationId);

            for (OperationInput input : inputs) {
                ProductOperation inputOperation = input.getInputOperation();

                if (inputOperation == null) {
                    throw new ValidationException("Input operation is null for input: " + input.getId());
                }

                if (!result.containsKey(inputOperation.getId())) {
                    queue.add(inputOperation);
                }
            }
        }

        return result;
    }

    private TechnologyProcessTemplateOperationResponse buildOperationGraph(
        TechnologyProcessTemplateOperation operation,
        Map<Long, List<TechnologyProcessTemplateInput>> inputsByOutputOperation,
        Set<Long> visitedPath
    ) {
        Long operationId = operation.getId();

        // Защита от циклических зависимостей.
        if (!visitedPath.add(operationId)) {
            throw new IllegalStateException(
                    "Circular dependency detected in technology process template. "
                            + "Operation ID: " + operationId
            );
        }

        try {
            List<TechnologyProcessTemplateInput> inputs =
                    inputsByOutputOperation.getOrDefault(
                            operationId,
                            Collections.emptyList()
                    );

            List<TechnologyProcessTemplateInputResponse> inputResponses =
                    inputs.stream()
                            .map(input -> {

                                TechnologyProcessTemplateOperation inputOperation =
                                        input.getInputTemplateOperation();

                                if (inputOperation == null) {
                                    throw new IllegalStateException(
                                            "Template input has no internal operation. "
                                                    + "Input ID: " + input.getId()
                                    );
                                }

                                // Рекурсивно строим входную операцию.
                                TechnologyProcessTemplateOperationResponse
                                        nestedOperation =
                                        buildOperationGraph(
                                                inputOperation,
                                                inputsByOutputOperation,
                                                visitedPath
                                        );

                                return new TechnologyProcessTemplateInputResponse(
                                        input.getId(),
                                        operationId,
                                        input.getQuantity(),
                                        input.getBaseQuantity(),
                                        input.getMeasure() != null
                                                ? MeasureResponse.from(input.getMeasure())
                                                : null,
                                        nestedOperation
                                );
                            })
                            .toList();

            return TechnologyProcessTemplateOperationResponse.fromEntity(
                    operation,
                    inputResponses
            );

        } finally {
            // Убираем операцию из текущего пути обхода.
            visitedPath.remove(operationId);
        }
    }
}