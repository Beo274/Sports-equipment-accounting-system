package ru.etu.sport.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ProductList;
import ru.etu.sport.model.entity.Product;
import ru.etu.sport.repository.ProductRepository;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void deleteProduct(Integer id) {
        productRepository.delete(id);
    }

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

    @Override
    public void updateClassId(Integer id, Integer parentId) {
        productRepository.updateParentId(id, parentId);
    }

    @Override
    public Integer addProduct(Product product) {
        return productRepository.save(product).getId();
    }
}
