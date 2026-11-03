package ru.etu.sport.operations.application;

import java.util.List;

import ru.etu.sport.operations.dto.ProductOperationDto;
import ru.etu.sport.operations.dto.ProductOperationReponse;
import ru.etu.sport.operations.dto.UpdateProductOperationDto;

public interface ProductOperationService {
    ProductOperationReponse create(ProductOperationDto dto);

    ProductOperationReponse update(Long id, UpdateProductOperationDto dto);

    void delete(Long id);

    ProductOperationReponse getById(Long id);

    List<ProductOperationReponse> getByProductId(Long id);
}
