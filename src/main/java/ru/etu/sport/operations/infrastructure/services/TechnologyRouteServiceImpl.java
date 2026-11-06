package ru.etu.sport.operations.infrastructure.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.bom.infrastructure.BomRepository;
import ru.etu.sport.bom.persistence.BomEntity;
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
    private final BomRepository bomRepository;

    @Override
    public TechnologyRouteResponse getFullRoute(Long productId) {
        Product rootProduct = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        return buildRouteTreeRecursive(rootProduct, new LinkedHashSet<>());
    }

    private TechnologyRouteResponse buildRouteTreeRecursive(Product product, Set<Integer> visited) {
        if (visited.contains(product.getId())) {
            throw new IllegalStateException("Циклическая зависимость в структуре изделия: "
                    + visited + " -> " + product.getId());
        }
        visited.add(product.getId());

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

        long ownSetupTime = operations.stream()
                .mapToLong(op -> op.getSetupTime() != null ? op.getSetupTime() : 0L)
                .sum();

        long ownPieceTime = operations.stream()
                .mapToLong(op -> op.getPieceTime() != null ? op.getPieceTime() : 0L)
                .sum();

        List<BomEntity> bomEntries = bomRepository.findAllByParentProduct(product);
        List<TechnologyRouteResponse> childrenRoutes = bomEntries.stream()
                .map(bom -> buildRouteTreeRecursive(bom.getComponent(), new LinkedHashSet<>(visited)))
                .toList();

        long totalSetupTime = ownSetupTime + childrenRoutes.stream()
                .mapToLong(TechnologyRouteResponse::totalSetupTime)
                .sum();

        long totalPieceTime = ownPieceTime + childrenRoutes.stream()
                .mapToLong(TechnologyRouteResponse::totalPieceTime)
                .sum();

        return new TechnologyRouteResponse(
                ProductResponse.from(product),
                totalSetupTime,
                totalPieceTime,
                routeOperations,
                childrenRoutes
        );
    }
}