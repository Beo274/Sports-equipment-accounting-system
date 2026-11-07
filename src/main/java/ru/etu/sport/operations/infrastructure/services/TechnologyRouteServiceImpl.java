package ru.etu.sport.operations.infrastructure.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.measure.MeasureRepository;
import ru.etu.sport.measure.dto.MeasureResponse;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.operations.application.TechnologyRouteService;
import ru.etu.sport.operations.dto.CreateOperationInputDto;
import ru.etu.sport.operations.dto.OperationInputResponse;
import ru.etu.sport.operations.dto.TechnologyRouteInput;
import ru.etu.sport.operations.dto.TechnologyRouteNode;
import ru.etu.sport.operations.dto.TechnologyRouteResponse;
import ru.etu.sport.operations.infrastructure.repository.OperationInputRepository;
import ru.etu.sport.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.operations.persistence.OperationInput;
import ru.etu.sport.operations.persistence.ProductOperation;
import ru.etu.sport.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class TechnologyRouteServiceImpl implements TechnologyRouteService {

    private final ProductRepository productRepository;
    private final ProductOperationRepository productOperationRepository;
    private final OperationInputRepository operationInputRepository;
    private final MeasureRepository measureRepository;

    @Override
    public TechnologyRouteResponse getFullRoute(Long productId) {
        Product product = this.productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        List<ProductOperation> rootOperations =
                productOperationRepository
                        .findByProduct_IdOrderByOperationNumberAsc(productId);

        if (rootOperations.isEmpty()) {
            return new TechnologyRouteResponse(ProductResponse.from(product), List.of());
        }

        List<TechnologyRouteNode> result = new ArrayList<>();

        for (ProductOperation operation : rootOperations) {
            result.add(buildRouteNode(operation, new HashSet<>()));
        }

        return new TechnologyRouteResponse(ProductResponse.from(product), result);
    }

    private TechnologyRouteNode buildRouteNode(
            ProductOperation operation,
            Set<Long> currentPath
    ) {
        Long operationId = operation.getId();

        // Проверяем, не вернулись ли мы к операции,
        // которая уже находится в текущей цепочке зависимостей.
        if (!currentPath.add(operationId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cycle occured in tech. route, operation id: "
                            + operationId
            );
        }

        List<OperationInput> operationInputs =
                operationInputRepository
                        .findByOutputOperation_IdOrderByIdAsc(operationId);

        List<TechnologyRouteInput> inputs = new ArrayList<>();

        for (OperationInput input : operationInputs) {

            TechnologyRouteNode inputNode = buildRouteNode(
                    input.getInputOperation(),
                    new HashSet<>(currentPath)
            );

            inputs.add(new TechnologyRouteInput(
                    input.getId(),
                    input.getQuantity(),
                    input.getBaseQuantity(),
                    MeasureResponse.from(input.getMeasure()),
                    inputNode
            ));
        }

        return new TechnologyRouteNode(
            operation.getId(),
            operation.getProduct().getId().longValue(),
            operation.getProduct().getName(),
            operation.getOperationNumber(),
            operation.getName(),
            operation.getOperationType() != null 
                        ? operation.getOperationType().getName()
                        : null,
            operation.getWorkCenter() != null
                        ? operation.getWorkCenter().getName()
                        : null,
                operation.getProfession() != null
                        ? operation.getProfession().getName()
                        : null,
                operation.getQualification() != null
                        ? operation.getQualification().getName()
                        : null,
            operation.getSetupTime(),
            operation.getPieceTime(),
            inputs
        );
    }

    // Получение всех входных ресурсов операции
    @Transactional(readOnly = true)
    public List<OperationInputResponse> getInputs(Long operationId) {

        // Проверяем существование операции
        getOperationOrThrow(operationId);

        return operationInputRepository
                .findByOutputOperation_IdOrderByIdAsc(operationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Добавление входного ресурса
    @Transactional
    public OperationInputResponse addInput(
            Long operationId,
            CreateOperationInputDto dto
    ) {
        ProductOperation outputOperation =
                getOperationOrThrow(operationId);

        ProductOperation inputOperation =
                getOperationOrThrow(dto.operationId());

        Measure measure = measureRepository.findById(dto.measureId().intValue())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Единица измерения не найдена"
                ));

        validateInput(outputOperation, inputOperation);

        // Проверяем дублирование связи
        if (operationInputRepository
                .existsByOutputOperation_IdAndInputOperation_Id(
                        operationId,
                        inputOperation.getId()
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Такая входная связь уже существует"
            );
        }

        // Проверяем, не образуется ли цикл
        if (createsCycle(
                outputOperation.getId(),
                inputOperation.getId(),
                null
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Добавление связи создаёт цикл в технологическом маршруте"
            );
        }

        OperationInput input = new OperationInput();

        input.setOutputOperation(outputOperation);
        input.setInputOperation(inputOperation);
        input.setQuantity(dto.quantity());
        input.setBaseQuantity(dto.baseQuantity());
        input.setMeasure(measure);

        OperationInput saved = operationInputRepository.save(input);

        return mapToResponse(saved);
    }

    // Изменение входного ресурса
    @Transactional
    public OperationInputResponse updateInput(
            Long operationId,
            Long inputId,
            CreateOperationInputDto request
    ) {
        // Находим связь именно в рамках указанной операции
        OperationInput input = operationInputRepository.findByIdAndOutputOperation_Id(inputId, operationId)
                .orElseThrow(() -> new EntityNotFoundException("Input not found with id: " + inputId));

        ProductOperation inputOperation =
                getOperationOrThrow(request.operationId());

        Measure measure = measureRepository.findById(request.measureId().intValue())
                .orElseThrow(() -> new EntityNotFoundException("Measure not found with id: " + request.measureId()));

        validateInput(input.getOutputOperation(), inputOperation);

        // Проверяем дублирование, исключая текущую запись
        if (operationInputRepository
                .existsByOutputOperation_IdAndInputOperation_IdAndIdNot(
                        operationId,
                        inputOperation.getId(),
                        inputId
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Input operation already exists"
            );
        }

        // При проверке цикла исключаем старую версию изменяемой связи
        if (createsCycle(
                operationId,
                inputOperation.getId(),
                inputId
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Creating cycle in technology route"
            );
        }

        input.setInputOperation(inputOperation);
        input.setQuantity(request.quantity());
        input.setBaseQuantity(request.baseQuantity());
        input.setMeasure(measure);

        OperationInput updated = operationInputRepository.save(input);

        return mapToResponse(updated);
    }

    // Удаление входного ресурса
    @Transactional
    public void deleteInput(Long operationId, Long inputId) {

        OperationInput input = operationInputRepository
                .findByIdAndOutputOperation_Id(inputId, operationId)
                .orElseThrow(() -> new EntityNotFoundException("Input operation not found for this operation"));

        operationInputRepository.delete(input);
    }

    private ProductOperation getOperationOrThrow(Long operationId) {
        return productOperationRepository.findById(operationId)
                .orElseThrow(() -> new EntityNotFoundException("Product operation not found, id: " + operationId));
    }

    private void validateInput(
            ProductOperation outputOperation,
            ProductOperation inputOperation
    ) {
        if (outputOperation.getId().equals(inputOperation.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Операция не может использовать собственный результат"
            );
        }
    }

    private OperationInputResponse mapToResponse(OperationInput input) {

        ProductOperation inputOperation = input.getInputOperation();

        return new OperationInputResponse(
                input.getId(),
                input.getOutputOperation().getId(),
                inputOperation.getId(),
                ProductResponse.from(inputOperation.getProduct()),
                inputOperation.getOperationNumber(),
                input.getQuantity(),
                input.getBaseQuantity(),
                MeasureResponse.from(input.getMeasure())
        );
    }

    /**
     * Проверяет, приведёт ли добавление связи
     * outputOperation -> inputOperation к циклу.
     *
     * ignoredInputId нужен при обновлении:
     * старую версию изменяемой связи не учитываем.
     */
    private boolean createsCycle(
            Long outputOperationId,
            Long inputOperationId,
            Long ignoredInputId
    ) {
        Set<Long> visited = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>();

        stack.push(inputOperationId);

        while (!stack.isEmpty()) {
            Long currentId = stack.pop();

            if (currentId.equals(outputOperationId)) {
                return true;
            }

            if (!visited.add(currentId)) {
                continue;
            }

            List<OperationInput> inputs =
                    operationInputRepository
                            .findByOutputOperation_IdOrderByIdAsc(currentId);

            for (OperationInput input : inputs) {

                if (ignoredInputId != null
                        && input.getId().equals(ignoredInputId)) {
                    continue;
                }

                stack.push(input.getInputOperation().getId());
            }
        }

        return false;
    }
}