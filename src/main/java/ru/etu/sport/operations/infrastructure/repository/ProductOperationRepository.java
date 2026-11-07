package ru.etu.sport.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.operations.persistence.ProductOperation;
import java.util.List;
import ru.etu.sport.model.entity.Product;

public interface ProductOperationRepository extends JpaRepository<ProductOperation, Long> {
    List<ProductOperation> findAllByProduct(Product product);

    List<ProductOperation> findByProduct_IdOrderByOperationNumberAsc(
        Long productId
    );
}
