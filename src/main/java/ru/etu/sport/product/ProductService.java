package ru.etu.sport.product;

import ru.etu.sport.model.dto.request.CreateProductDto;
import ru.etu.sport.model.dto.response.ProductList;
import ru.etu.sport.model.dto.response.ProductWithParamsResponse;

public interface ProductService {
    
    void deleteProduct(Integer id);

    ProductList getProducts(Integer classId, Integer limit, Integer offset);

    void updateClassId(Integer id, Integer parentId);

    Integer addProduct(CreateProductDto createProductDto);

    ProductWithParamsResponse getByIdWithParams(Integer id);
}
