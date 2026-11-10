package ru.etu.sport.techroute.calc.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;
import ru.etu.sport.techroute.calc.model.dto.MaterialResponseDto;
import ru.etu.sport.techroute.calc.model.dto.OperationCalcStandartsResponseDto;
import ru.etu.sport.techroute.operations.domain.OperationKind;
import ru.etu.sport.techroute.operations.infrastructure.repository.OperationInputRepository;
import ru.etu.sport.techroute.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.techroute.operations.persistence.OperationInput;
import ru.etu.sport.techroute.operations.persistence.ProductOperation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class CalculationService {
    public final ProductRepository productRepository;
    public final ProductOperationRepository productOperationRepository;
    public final OperationInputRepository operationInputRepository;

    public CalculationService(
            ProductRepository productRepository,
            ProductOperationRepository productOperationRepository,
            OperationInputRepository operationInputRepository
    ) {
        this.productRepository = productRepository;
        this.operationInputRepository = operationInputRepository;
        this.productOperationRepository = productOperationRepository;
    }

    public OperationCalcStandartsResponseDto getOperationRates(Integer operationId, Integer number) {
        ProductOperation operation = productOperationRepository.findById(operationId.longValue())
                .orElseThrow(EntityNotFoundException::new);

        Deque<ProductOperation> operationStack = operationInputRepository.findInputOperationByOutputOperation(operation);
        operationStack.push(operation);

        List<MaterialResponseDto> materials = new LinkedList<>();
        while (!operationStack.isEmpty()) {

            ProductOperation op = operationStack.pop();

            List<OperationInput> inputs =
                    operationInputRepository.findByOutputOperation(op);

            for (OperationInput input : inputs) {

                BigDecimal requiredQuantity = BigDecimal.valueOf(number)
                        .multiply(input.getQuantity())
                        .divide(
                                input.getBaseQuantity(),
                                0,
                                RoundingMode.CEILING
                        );

                ProductOperation inputOperation = input.getInputOperation();

                materials.add(new MaterialResponseDto(
                        inputOperation.getProduct().getName(),
                        requiredQuantity.intValue()
                ));

            }
        }

        return new OperationCalcStandartsResponseDto(
                operation.getName(),
                number,
                materials
        );
    }

    public OperationCalcStandartsResponseDto getTotalMaterialRates(Integer productId, Integer number) {
        if (number == null || number <= 0) {
            throw new IllegalArgumentException(
                    "Количество изделий должно быть больше нуля"
            );
        }

        Product product = productRepository.findById(productId.longValue())
                .orElseThrow(EntityNotFoundException::new);

        List<ProductOperation> operations =
                productOperationRepository
                        .findByProduct_IdOrderByOperationNumberAsc(productId.longValue());

        if (operations.isEmpty()) {
            throw new EntityNotFoundException(
                    "Для изделия не найдены технологические операции"
            );
        }

        ProductOperation finalOperation =
                operations.get(operations.size() - 1);

        Map<Long, BigDecimal> materialTotals = new HashMap<>();

        Map<Long, String> materialNames = new HashMap<>();

        collectPurchasedMaterials(
                finalOperation,
                BigDecimal.valueOf(number),
                materialTotals,
                materialNames,
                new HashSet<>()
        );

        List<MaterialResponseDto> materials = new LinkedList<>();

        for (Map.Entry<Long, BigDecimal> entry : materialTotals.entrySet()) {

            int requiredQuantity = entry.getValue()
                    .setScale(0, RoundingMode.CEILING)
                    .intValueExact();

            materials.add(new MaterialResponseDto(
                    materialNames.get(entry.getKey()),
                    requiredQuantity
            ));
        }

        return new OperationCalcStandartsResponseDto(
                product.getName(),
                number,
                materials
        );
    }

    private void collectPurchasedMaterials(
            ProductOperation operation,
            BigDecimal outputQuantity,
            Map<Long, BigDecimal> materialTotals,
            Map<Long, String> materialNames,
            Set<Long> currentPath
    ) {

        if (!currentPath.add(operation.getId())) {
            throw new IllegalStateException(
                    "Обнаружена циклическая зависимость в технологическом маршруте"
            );
        }

        try {
            List<OperationInput> inputs =
                    operationInputRepository.findByOutputOperation(operation);

            for (OperationInput input : inputs) {

                BigDecimal requiredQuantity = input.getQuantity()
                        .multiply(outputQuantity)
                        .divide(
                                input.getBaseQuantity(),
                                MathContext.DECIMAL128
                        );

                ProductOperation inputOperation =
                        input.getInputOperation();

                if (inputOperation.getKind() == OperationKind.PURCHASE) {

                    Long materialId = inputOperation.getId();

                    materialTotals.merge(
                            materialId,
                            requiredQuantity,
                            BigDecimal::add
                    );

                    materialNames.putIfAbsent(
                            materialId,
                            inputOperation.getProduct().getName()
                    );

                } else {

                    collectPurchasedMaterials(
                            inputOperation,
                            requiredQuantity,
                            materialTotals,
                            materialNames,
                            currentPath
                    );
                }
            }
        } finally {
            currentPath.remove(operation.getId());
        }
    }

    public Long getTotalLaborRates(Integer productId, Integer number) {
        if (number == null || number <= 0) {
            throw new IllegalArgumentException(
                    "Количество изделий должно быть больше нуля"
            );
        }

        List<ProductOperation> operations =
                productOperationRepository
                        .findByProduct_IdOrderByOperationNumberAsc(
                                productId.longValue()
                        );

        if (operations.isEmpty()) {
            throw new EntityNotFoundException(
                    "Для изделия не найдены технологические операции"
            );
        }

        ProductOperation finalOperation =
                operations.get(operations.size() - 1);

        return calculateLaborTime(
                finalOperation,
                BigDecimal.valueOf(number),
                new HashSet<>()
        );
    }

    private Long calculateLaborTime(
            ProductOperation operation,
            BigDecimal outputQuantity,
            Set<Long> currentPath
    ) {
        if (!currentPath.add(operation.getId())) {
            throw new IllegalStateException(
                    "Обнаружена циклическая зависимость в технологическом маршруте"
            );
        }

        try {
            BigDecimal setupTime = BigDecimal.valueOf(
                    operation.getSetupTime() == null
                            ? 0L
                            : operation.getSetupTime()
            );

            BigDecimal pieceTime = BigDecimal.valueOf(
                    operation.getPieceTime() == null
                            ? 0L
                            : operation.getPieceTime()
            );

            BigDecimal totalTime = setupTime.add(
                    pieceTime.multiply(outputQuantity)
            );

            List<OperationInput> inputs =
                    operationInputRepository.findByOutputOperation(operation);

            for (OperationInput input : inputs) {
                ProductOperation inputOperation =
                        input.getInputOperation();

                if (inputOperation.getKind() == OperationKind.PURCHASE) {
                    continue;
                }


                BigDecimal requiredQuantity = input.getQuantity()
                        .multiply(outputQuantity)
                        .divide(
                                input.getBaseQuantity(),
                                MathContext.DECIMAL128
                        );


                requiredQuantity = requiredQuantity.setScale(
                        0,
                        RoundingMode.CEILING
                );

                Long inputTime = calculateLaborTime(
                        inputOperation,
                        requiredQuantity,
                        currentPath
                );

                totalTime = totalTime.add(
                        BigDecimal.valueOf(inputTime)
                );
            }

            return totalTime
                    .setScale(0, RoundingMode.CEILING)
                    .longValueExact();

        } finally {
            currentPath.remove(operation.getId());
        }
    }

}
