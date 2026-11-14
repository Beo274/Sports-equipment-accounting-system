package ru.etu.sport.techroute.calc.service;

import jakarta.persistence.EntityNotFoundException;
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

/**
 * Расчёт материальных и трудовых норм по графу технологического маршрута.
 */
@Service
public class CalculationService {
    private final ProductRepository productRepository;
    private final ProductOperationRepository productOperationRepository;
    private final OperationInputRepository operationInputRepository;

    public CalculationService(
            ProductRepository productRepository,
            ProductOperationRepository productOperationRepository,
            OperationInputRepository operationInputRepository
    ) {
        this.productRepository = productRepository;
        this.operationInputRepository = operationInputRepository;
        this.productOperationRepository = productOperationRepository;
    }

    /**
     * Материальные нормы для операции с учётом всех вложенных технологических
     * операций. Промежуточные операции раскрываются до закупаемых материалов.
     */
    public OperationCalcStandartsResponseDto getOperationRates(Integer operationId, Integer number) {
        validateQuantity(number);

        ProductOperation operation = productOperationRepository.findById(operationId.longValue())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Операция не найдена: " + operationId
                ));

        Map<Long, BigDecimal> materialTotals = new LinkedHashMap<>();
        Map<Long, String> materialNames = new LinkedHashMap<>();

        collectPurchasedMaterials(
                operation,
                BigDecimal.valueOf(number),
                materialTotals,
                materialNames,
                new HashSet<>()
        );

        return new OperationCalcStandartsResponseDto(
                operation.getName(),
                number,
                toMaterialResponses(materialTotals, materialNames)
        );
    }

    /**
     * Сводные материальные нормы изделия. Расчёт начинается с последней
     * (выпускающей изделие) операции маршрута и рекурсивно раскрывает её входы.
     */
    public OperationCalcStandartsResponseDto getTotalMaterialRates(Integer productId, Integer number) {
        validateQuantity(number);

        Product product = productRepository.findById(productId.longValue())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Изделие не найдено: " + productId
                ));

        List<ProductOperation> operations =
                productOperationRepository.findByProduct_IdOrderByOperationNumberAsc(productId.longValue());

        if (operations.isEmpty()) {
            throw new EntityNotFoundException(
                    "Для изделия не найдены технологические операции"
            );
        }

        ProductOperation finalOperation = operations.get(operations.size() - 1);
        Map<Long, BigDecimal> materialTotals = new LinkedHashMap<>();
        Map<Long, String> materialNames = new LinkedHashMap<>();

        collectPurchasedMaterials(
                finalOperation,
                BigDecimal.valueOf(number),
                materialTotals,
                materialNames,
                new HashSet<>()
        );

        return new OperationCalcStandartsResponseDto(
                product.getName(),
                number,
                toMaterialResponses(materialTotals, materialNames)
        );
    }

    /**
     * Обходит входы операции.
     *
     * Норма входного ресурса = quantity * требуемый выпуск / baseQuantity.
     * Для технологической входной операции количество выполнений округляется
     * вверх сразу на этом переходе: технологическую операцию нельзя выполнить
     * дробное число раз. Для закупаемого материала дробные нормы суммируются,
     * а округление вверх выполняется только в итоговом результате.
     */
    private void collectPurchasedMaterials(
            ProductOperation operation,
            BigDecimal outputQuantity,
            Map<Long, BigDecimal> materialTotals,
            Map<Long, String> materialNames,
            Set<Long> currentPath
    ) {
        if (!currentPath.add(operation.getId())) {
            throw new IllegalStateException(
                    "Обнаружена циклическая зависимость в технологическом маршруте, операция: "
                            + operation.getId()
            );
        }

        try {
            List<OperationInput> inputs =
                    operationInputRepository.findByOutputOperation(operation);

            for (OperationInput input : inputs) {
                BigDecimal baseQuantity = input.getBaseQuantity();
                if (baseQuantity == null || baseQuantity.signum() <= 0) {
                    throw new IllegalStateException(
                            "Базовое количество должно быть больше нуля. Вход операции: "
                                    + input.getId()
                    );
                }

                BigDecimal requiredQuantity = input.getQuantity()
                        .multiply(outputQuantity)
                        .divide(baseQuantity, MathContext.DECIMAL128);

                ProductOperation inputOperation = input.getInputOperation();
                if (inputOperation == null) {
                    throw new IllegalStateException(
                            "Не задана входная операция для связи: " + input.getId()
                    );
                }

                if (inputOperation.getKind() == OperationKind.PURCHASE) {
                    Product material = inputOperation.getProduct();
                    if (material == null || material.getId() == null) {
                        throw new IllegalStateException(
                                "У закупаемой операции не задан продукт. Операция: "
                                        + inputOperation.getId()
                        );
                    }

                    Long materialId = material.getId().longValue();
                    materialTotals.merge(materialId, requiredQuantity, BigDecimal::add);
                    materialNames.putIfAbsent(materialId, material.getName());
                } else {
                    // Производственная операция неделима — требуется целое
                    // количество её выполнений на каждом уровне маршрута.
                    BigDecimal operationCount = requiredQuantity.setScale(
                            0,
                            RoundingMode.CEILING
                    );

                    collectPurchasedMaterials(
                            inputOperation,
                            operationCount,
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

    private List<MaterialResponseDto> toMaterialResponses(
            Map<Long, BigDecimal> materialTotals,
            Map<Long, String> materialNames
    ) {
        return materialTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new MaterialResponseDto(
                        materialNames.get(entry.getKey()),
                        entry.getValue()
                                .setScale(0, RoundingMode.CEILING)
                                .intValueExact()
                ))
                .toList();
    }

    public Long getTotalLaborRates(Integer productId, Integer number) {
        validateQuantity(number);

        List<ProductOperation> operations =
                productOperationRepository.findByProduct_IdOrderByOperationNumberAsc(
                        productId.longValue()
                );

        if (operations.isEmpty()) {
            throw new EntityNotFoundException(
                    "Для изделия не найдены технологические операции"
            );
        }

        ProductOperation finalOperation = operations.get(operations.size() - 1);

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
                    operation.getSetupTime() == null ? 0L : operation.getSetupTime()
            );
            BigDecimal pieceTime = BigDecimal.valueOf(
                    operation.getPieceTime() == null ? 0L : operation.getPieceTime()
            );

            BigDecimal totalTime = setupTime.add(pieceTime.multiply(outputQuantity));

            List<OperationInput> inputs =
                    operationInputRepository.findByOutputOperation(operation);

            for (OperationInput input : inputs) {
                ProductOperation inputOperation = input.getInputOperation();
                if (inputOperation.getKind() == OperationKind.PURCHASE) {
                    continue;
                }

                BigDecimal baseQuantity = input.getBaseQuantity();
                if (baseQuantity == null || baseQuantity.signum() <= 0) {
                    throw new IllegalStateException(
                            "Базовое количество должно быть больше нуля. Вход операции: "
                                    + input.getId()
                    );
                }

                BigDecimal requiredQuantity = input.getQuantity()
                        .multiply(outputQuantity)
                        .divide(baseQuantity, MathContext.DECIMAL128)
                        .setScale(0, RoundingMode.CEILING);

                Long inputTime = calculateLaborTime(
                        inputOperation,
                        requiredQuantity,
                        currentPath
                );
                totalTime = totalTime.add(BigDecimal.valueOf(inputTime));
            }

            return totalTime.setScale(0, RoundingMode.CEILING).longValueExact();
        } finally {
            currentPath.remove(operation.getId());
        }
    }

    private void validateQuantity(Integer number) {
        if (number == null || number <= 0) {
            throw new IllegalArgumentException(
                    "Количество изделий/операций должно быть больше нуля"
            );
        }
    }
}
