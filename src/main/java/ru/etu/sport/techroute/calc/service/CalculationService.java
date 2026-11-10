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
import java.math.RoundingMode;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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

}
