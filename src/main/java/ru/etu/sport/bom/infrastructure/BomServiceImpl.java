package ru.etu.sport.bom.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.bom.application.BomService;
import ru.etu.sport.bom.dto.BomResponse;
import ru.etu.sport.bom.dto.CreateBomRequest;
import ru.etu.sport.bom.dto.UpdateBomRequest;
import ru.etu.sport.bom.persistence.BomEntity;
import ru.etu.sport.change.entities.ProductChange;
import ru.etu.sport.change.entities.dto.ProductChangeRequest;
import ru.etu.sport.change.services.ChangeService;
import ru.etu.sport.change.services.impls.ChangeServiceImpl;
import ru.etu.sport.measure.MeasureRepository;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;

@Service 
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
                "BOM was created with parent ID = " + parent.getId().toString()
        ));

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
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + request.getParentProductId())));
        }
        if (request.getComponentId() != null) {
            entity.setComponent(productRepository.findById(request.getComponentId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + request.getComponentId())));
        }
        if (request.getMeasureUnitId() != null) {
            entity.setMeasure(measureRepository.findById(request.getMeasureUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Measure not found: " + request.getMeasureUnitId())));
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
                request.getParentProductId(),
                "BOM parent changed: old parent ID = "
                    + oldParentProduct.getId()
                    + "new parent ID = "
                    + entity.getParentProduct().getId()
        ));
        }

        if (!entity.getComponent().equals(oldComponent)) {
            changeService.create(new ProductChangeRequest(
                    LocalDate.now(),
                    request.getParentProductId(),
                    "BOM component changed: old component ID = "
                            + oldComponent.getId()
                            + "new component ID = "
                            + entity.getComponent().getId()
            ));
        }

        if (!entity.getQuantity().equals(oldQuantity)) {
            changeService.create(new ProductChangeRequest(
                    LocalDate.now(),
                    request.getParentProductId(),
                    "BOM component's quantity changed: old quantity = "
                            + oldQuantity
                            + "new quantity = "
                            + entity.getQuantity()
            ));
        }

        return BomResponse.from(entity);
    }

    @Override
    @Transactional 
    public void remove(Integer id) {
        if (!bomRepository.existsById(id)) {
            throw new EntityNotFoundException("Bom not found with id: "+ id);
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
                .orElseThrow(() -> new EntityNotFoundException("Bom not find with id: "+ id));
        return BomResponse.from(found);
    }
}
