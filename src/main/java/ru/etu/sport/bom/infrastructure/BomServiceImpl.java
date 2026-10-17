package ru.etu.sport.bom.infrastructure;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.bom.application.BomService;
import ru.etu.sport.bom.dto.BomItemDto;
import ru.etu.sport.bom.dto.BomResponse;
import ru.etu.sport.bom.dto.BomTreeDto;
import ru.etu.sport.bom.dto.CreateBomRequest;
import ru.etu.sport.bom.dto.UpdateBomRequest;
import ru.etu.sport.bom.persistence.BomEntity;
import ru.etu.sport.change.entities.dto.ProductChangeRequest;
import ru.etu.sport.change.services.ChangeService;
import ru.etu.sport.measure.MeasureRepository;
import ru.etu.sport.model.dto.response.ProductResponse;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;
@Service
@Slf4j
@RequiredArgsConstructor
public class BomServiceImpl implements BomService {

    private final BomRepository bomRepository;
    private final ProductRepository productRepository;
    private final MeasureRepository measureRepository;
    private final ChangeService changeService;

    @Override
    @Transactional
    public BomResponse create(CreateBomRequest request) {
        Product parent = productRepository.findById(request.getParentProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + request.getParentProductId()));
        Product component = productRepository.findById(request.getComponentId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + request.getComponentId()));
        Measure measure = measureRepository.findById(request.getMeasureUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Measure not found: " + request.getMeasureUnitId()));

        BomEntity entity = BomEntity.builder()
                .parentProduct(parent)
                .component(component)
                .measure(measure)
                .quantity(request.getQuantity())
                .forQuantity(request.getForQuantity())
                .isBase(parent.getBaseProduct() == null ? Boolean.TRUE : Boolean.FALSE)
                .build();

        changeService.create(new ProductChangeRequest(
                LocalDate.now(),
                Long.valueOf(parent.getId()),
                "BOM was created with parent ID = " + parent.getId().toString()));

        return BomResponse.from(bomRepository.save(entity));
    }

    @Override
    @Transactional
    public BomResponse update(Integer id, UpdateBomRequest request) {
        BomEntity entity = bomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bom not found: " + id));

        var oldParentProduct = entity.getParentProduct();
        var oldComponent = entity.getComponent();
        var oldQuantity = entity.getQuantity();

        if (request.getParentProductId() != null) {
            entity.setParentProduct(productRepository.findById(request.getParentProductId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Product not found: " + request.getParentProductId())));
        }
        if (request.getComponentId() != null) {
            Product newComponent = productRepository.findById(request.getComponentId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + request.getComponentId()));
            entity.setComponent(newComponent);

            propagateComponentChange(request.getParentProductId(), oldComponent.getId().longValue(),
                    newComponent.getId().longValue());
        }
        if (request.getMeasureUnitId() != null) {
            entity.setMeasure(measureRepository.findById(request.getMeasureUnitId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Measure not found: " + request.getMeasureUnitId())));
        }
        if (request.getQuantity() != null) {
            entity.setQuantity(request.getQuantity());
        }
        if (request.getForQuantity() != null) {
            entity.setForQuantity(request.getForQuantity());
        }
        if (request.getIsBase() != null) {
            entity.setIsBase(request.getIsBase());
        }

        if (!entity.getParentProduct().equals(oldParentProduct)) {
            changeService.create(new ProductChangeRequest(
                    LocalDate.now(),
                    Long.valueOf(entity.getParentProduct().getId()),
                    "BOM parent changed: old parent ID = "
                            + oldParentProduct.getId()
                            + " new parent ID = "
                            + entity.getParentProduct().getId()));
        }

        if (!entity.getComponent().equals(oldComponent)) {
            changeService.create(new ProductChangeRequest(
                    LocalDate.now(),
                    Long.valueOf(entity.getParentProduct().getId()),
                    "BOM component changed: old component ID = "
                            + oldComponent.getId()
                            + " new component ID = "
                            + entity.getComponent().getId()));
        }

        if (!entity.getQuantity().equals(oldQuantity)) {
            changeService.create(new ProductChangeRequest(
                    LocalDate.now(),
                    Long.valueOf(entity.getParentProduct().getId()),
                    "BOM component's quantity changed: old quantity = "
                            + oldQuantity
                            + " new quantity = "
                            + entity.getQuantity()));
        }

        return BomResponse.from(entity);
    }

    @Override
    @Transactional
    public void remove(Integer id) {
        if (!bomRepository.existsById(id)) {
            throw new EntityNotFoundException("Bom not found with id: " + id);
        }
        bomRepository.deleteById(id);
    }

    @Override
    public List<BomResponse> list() {
        return bomRepository.findAll()
                .stream()
                .map(entity -> BomResponse.from(entity))
                .toList();
    }

    @Override
    public List<BomResponse> list(List<Integer> ids) {
        return bomRepository.findAllById(ids)
                .stream()
                .map(entity -> BomResponse.from(entity))
                .toList();
    }

    @Override
    public BomResponse getById(Integer id) {
        BomEntity found = bomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bom not find with id: " + id));
        return BomResponse.from(found);
    }

    @Transactional
    public void propagateComponentChange(
            Long baseProductId,
            Long oldComponentId,
            Long newComponentId) {
        Product baseProduct = productRepository.findById(baseProductId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found: " + baseProductId));

        Product newComponent = productRepository
                .findById(newComponentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Component not found: " + newComponentId));

        Queue<Product> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(baseProduct);

        while (!queue.isEmpty()) {
            Product current = queue.poll();

            if (!visited.add(current.getId())) {
                continue;
            }

            List<BomEntity> boms = bomRepository.findAllByParentProductAndComponent_Id(
                    current,
                    oldComponentId);

            for (BomEntity bom : boms) {
                bom.setComponent(newComponent);
            }

            List<Product> children = productRepository.findAllByBaseProduct(current);

            queue.addAll(children);
        }
    }

    @Override
    @Transactional
    public void copyBom(Long baseProductId, Long newProductId) {
        Product baseProduct = productRepository.findById(baseProductId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + baseProductId));
        Product newProduct = productRepository.findById(newProductId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + newProductId));
        // Находим все BOM для основного продукта
        List<BomEntity> baseBoms = bomRepository.findAllByParentProduct(baseProduct);
        // Копируем все BOM для модификации
        for (BomEntity bomEntity : baseBoms) {
            BomEntity newProductBom = new BomEntity();
            newProductBom.setParentProduct(newProduct);
            newProductBom.setComponent(bomEntity.getComponent());
            newProductBom.setQuantity(bomEntity.getQuantity());
            newProductBom.setForQuantity(bomEntity.getForQuantity());
            newProductBom.setMeasure(bomEntity.getMeasure());
            newProductBom.setIsBase(bomEntity.getIsBase());
            bomRepository.save(newProductBom);
        }
    }

    @Override
    public BomTreeDto findBomTree(Integer productId) {
        Product root = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        return buildBomTreeRecursive(root, 1.0, null, new LinkedHashSet<>());
    }

    private BomTreeDto buildBomTreeRecursive(
            Product currentProduct,
            double currentQty,
            Measure measure,
            Set<Integer> currentPath
    ) {
        if (currentPath.contains(currentProduct.getId())) {
            throw new IllegalStateException("Циклическая зависимость обнаружена: "
                    + currentPath + " -> " + currentProduct.getId());
        }

        currentPath.add(currentProduct.getId());

        List<BomEntity> childrenBoms = bomRepository.findAllByParentProduct(currentProduct);
        List<BomTreeDto> children = new ArrayList<>();

        for (BomEntity bom : childrenBoms) {
            double componentQty = (double) bom.getQuantity() / bom.getForQuantity();
            children.add(buildBomTreeRecursive(
                    bom.getComponent(),
                    componentQty,
                    bom.getMeasure(),
                    new LinkedHashSet<>(currentPath)
            ));
        }

        return new BomTreeDto(
                ru.etu.sport.model.dto.response.ProductResponse.from(currentProduct),
                currentQty,
                measure != null ? ru.etu.sport.measure.dto.MeasureResponse.from(measure) : null,
                children
        );
    }

    @Override
    public List<BomItemDto> findBomLeaves(Integer productId) {
        Product root = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        List<BomItemDto> leaves = new ArrayList<>();
        collectLeavesRecursive(root, 1.0, null, new HashSet<>(), leaves);
        return leaves;
    }

    private void collectLeavesRecursive(
            Product currentProduct,
            double multiplier,
            Measure measure,
            Set<Integer> currentPath,
            List<BomItemDto> leaves
    ) {
        if (currentPath.contains(currentProduct.getId())) {
            throw new IllegalStateException("Цикл в структуре изделия для ID = " + currentProduct.getId());
        }

        currentPath.add(currentProduct.getId());
        List<BomEntity> directBoms = bomRepository.findAllByParentProduct(currentProduct);

        if (directBoms.isEmpty()) {
            leaves.add(new BomItemDto(
                    ru.etu.sport.model.dto.response.ProductResponse.from(currentProduct),
                    multiplier,
                    measure != null ? ru.etu.sport.measure.dto.MeasureResponse.from(measure) : null
            ));
        } else {
            for (BomEntity bom : directBoms) {
                double factor = (double) bom.getQuantity() / bom.getForQuantity();
                collectLeavesRecursive(
                        bom.getComponent(),
                        multiplier * factor,
                        bom.getMeasure(),
                        new HashSet<>(currentPath),
                        leaves
                );
            }
        }
    }


    @Override
    public List<BomItemDto> calcBom(Integer productId, double quantity) {
        return calcBom(productId, quantity, null);
    }


    @Override
    public List<BomItemDto> calcBom(Integer productId, double quantity, Integer classId) {
        Product root = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        Map<Integer, BomAccumulator> aggregatedMap = new LinkedHashMap<>();

        calculateBOMRecursive(root, quantity, new HashSet<>(), aggregatedMap, classId);

        return aggregatedMap.values().stream()
                .map(acc -> new BomItemDto(
                        ru.etu.sport.model.dto.response.ProductResponse.from(acc.product),
                        acc.totalQuantity,
                        acc.measure != null ? ru.etu.sport.measure.dto.MeasureResponse.from(acc.measure) : null
                ))
                .toList();
    }

    private void calculateBOMRecursive(
            Product currentProduct,
            double currentMultiplier,
            Set<Integer> currentPath,
            Map<Integer, BomAccumulator> aggregatedMap,
            Integer targetClassId
    ) {
        if (currentPath.contains(currentProduct.getId())) {
            throw new IllegalStateException("Обнаружен цикл в BOM для продукта: " + currentProduct.getId());
        }

        currentPath.add(currentProduct.getId());
        List<BomEntity> directBoms = bomRepository.findAllByParentProduct(currentProduct);

        for (BomEntity bom : directBoms) {
            Product component = bom.getComponent();
            double factor = (double) bom.getQuantity() / bom.getForQuantity();
            double neededForCurrentLevel = currentMultiplier * factor;

            List<BomEntity> subBoms = bomRepository.findAllByParentProduct(component);

            if (subBoms.isEmpty()) {
                boolean matchesClass = (targetClassId == null) ||
                        (component.getProductClass() != null && component.getProductClass().getId().equals(targetClassId));

                if (matchesClass) {
                    aggregatedMap.compute(component.getId(), (id, existing) -> {
                        if (existing == null) {
                            return new BomAccumulator(component, neededForCurrentLevel, bom.getMeasure());
                        } else {
                            existing.totalQuantity += neededForCurrentLevel;
                            return existing;
                        }
                    });
                }
            } else {
                calculateBOMRecursive(
                        component,
                        neededForCurrentLevel,
                        new HashSet<>(currentPath),
                        aggregatedMap,
                        targetClassId
                );
            }
        }
    }

    private static class BomAccumulator {
        Product product;
        double totalQuantity;
        Measure measure;

        BomAccumulator(Product product, double totalQuantity, Measure measure) {
            this.product = product;
            this.totalQuantity = totalQuantity;
            this.measure = measure;
        }
    }
}
