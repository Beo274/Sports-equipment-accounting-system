package ru.etu.sport.change.services;

import ru.etu.sport.change.entities.dto.ProductChangeRequest;
import ru.etu.sport.change.entities.dto.ProductChangeResponse;
import ru.etu.sport.change.entities.dto.UpdateProductChangeRequest;

import java.util.List;

public interface ChangeService {
    ProductChangeResponse create(ProductChangeRequest request);

    ProductChangeResponse update(Long id, UpdateProductChangeRequest request);

    Long delete(Long id);

    List<ProductChangeResponse> getAll();

    List<ProductChangeResponse> getForProduct(Long product_id);
}
