package ru.etu.sport.print.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ProductList;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.model.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductsListServiceImpl implements ProductsListService {

    private final ProductRepository productRepository;

    @Override
    public ProductList getProducts(Integer classId, Integer limit, Integer offset) {
        List<Product> products;
        Integer count;

        Pageable pageable = PageRequest.of(offset / limit, limit);

        if (classId == null) {
            products = this.productRepository.getProducts(pageable);
            count = this.productRepository.countProducts();
        } else {
            products = this.productRepository.findByClassId(classId, pageable);
            count = this.productRepository.countProducts(classId);
        }

        return new ProductList(count, limit, offset, products);
    }
}
