package ru.etu.sport.bom.infrastructure;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.etu.sport.bom.application.BomService;
import ru.etu.sport.bom.dto.BomResponse;
import ru.etu.sport.bom.dto.CreateBomRequest;
import ru.etu.sport.bom.dto.UpdateBomRequest;
import ru.etu.sport.bom.persistence.BomEntity;
import ru.etu.sport.measure.MeasureRepository;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.product.ProductRepository;
import ru.etu.sport.bom.application.exceptions.NotFoundException;

@Service 
@RequiredArgsConstructor 
public class BomServiceImpl implements BomService {

    private final BomRepository bomRepository;
    private final ProductRepository productRepository;
    private final MeasureRepository measureRepository;

    @Override
    @Transactional
    public BomResponse create(CreateBomRequest request) {
        Product parent = productRepository.findById(request.getParentProductId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + request.getParentProductId()));
        Product component = productRepository.findById(request.getComponentId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + request.getComponentId()));
        Measure measure = measureRepository.findById(request.getMeasureUnitId())
                .orElseThrow(() -> new NotFoundException("Measure not found: " + request.getMeasureUnitId()));

        BomEntity entity = BomEntity.builder()
                .parentProduct(parent)
                .component(component)
                .measure(measure)
                .quantity(request.getQuantity())
                .forQuantity(request.getForQuantity())
                .isBase(Boolean.TRUE.equals(request.getIsBase()))
                .build();

        return BomResponse.from(bomRepository.save(entity));
    }

    @Override
    @Transactional
    public BomResponse update(Integer id, UpdateBomRequest request) {
        BomEntity entity = bomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bom not found: " + id));

        if (request.getParentProductId() != null) {
            entity.setParentProduct(productRepository.findById(request.getParentProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + request.getParentProductId())));
        }
        if (request.getComponentId() != null) {
            entity.setComponent(productRepository.findById(request.getComponentId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + request.getComponentId())));
        }
        if (request.getMeasureUnitId() != null) {
            entity.setMeasure(measureRepository.findById(request.getMeasureUnitId())
                    .orElseThrow(() -> new NotFoundException("Measure not found: " + request.getMeasureUnitId())));
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

        return BomResponse.from(entity);
    }

    @Override
    @Transactional 
    public void remove(Integer id) {
        if (!bomRepository.existsById(id)) {
            throw new NotFoundException("Bom not found with id: "+ id);
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
                .orElseThrow(() -> new NotFoundException("Bom not find with id: "+ id));
        return BomResponse.from(found);
    }
}
