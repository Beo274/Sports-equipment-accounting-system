package ru.etu.sport.print.service;

import ru.etu.sport.model.dto.response.ProductList;

public interface ProductsListService {
    ProductList getProducts(Integer classId, Integer limit, Integer offset);
}
