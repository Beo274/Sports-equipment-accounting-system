package ru.etu.sport.operations.infrastructure.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.operations.application.TechnologyRouteService;
import ru.etu.sport.operations.dto.OperationResourceResponse;
import ru.etu.sport.operations.dto.TechnologyRouteOperationResponse;
import ru.etu.sport.operations.dto.TechnologyRouteResponse;
import ru.etu.sport.operations.infrastructure.repository.OperationResourceRepository;
import ru.etu.sport.operations.infrastructure.repository.ProductOperationRepository;
import ru.etu.sport.operations.persistence.ProductOperation;
import ru.etu.sport.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class TechnologyRouteServiceImpl implements TechnologyRouteService {

    private final ProductRepository productRepository;
    private final ProductOperationRepository productOperationRepository;
    private final OperationResourceRepository operationResourceRepository;

    @Override
    public TechnologyRouteResponse getFullRoute(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        List<ProductOperation> operations = productOperationRepository.findAllByProduct(product);
        operations.sort(Comparator.comparingLong(ProductOperation::getOperationNumber));

        List<Long> operationIds = operations.stream()
                .map(ProductOperation::getId)
                .toList();

        Map<Long, List<OperationResourceResponse>> resourcesByOperationId = operationIds.isEmpty()
                ? Collections.emptyMap()
                : operationResourceRepository.findAllByProductOperationIdIn(operationIds)
                .stream()
                .map(OperationResourceResponse::fromEntity)
                .collect(Collectors.groupingBy(OperationResourceResponse::productOperationId));

        List<TechnologyRouteOperationResponse> routeOperations = operations.stream()
                .map(op -> TechnologyRouteOperationResponse.fromEntity(
                        op,
                        resourcesByOperationId.getOrDefault(op.getId(), Collections.emptyList())
                ))
                .toList();

        long totalSetupTime = operations.stream()
                .mapToLong(op -> op.getSetupTime() != null ? op.getSetupTime() : 0L)
                .sum();

        long totalPieceTime = operations.stream()
                .mapToLong(op -> op.getPieceTime() != null ? op.getPieceTime() : 0L)
                .sum();

        return new TechnologyRouteResponse(
                ProductResponse.from(product),
                totalSetupTime,
                totalPieceTime,
                routeOperations
        );
    }
}