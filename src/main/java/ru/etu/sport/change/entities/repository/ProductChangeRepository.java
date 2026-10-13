package ru.etu.sport.change.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.change.entities.ProductChange;
import ru.etu.sport.model.entity.Product;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ProductChangeRepository extends JpaRepository<ProductChange, Long> {
    List<ProductChange> findAllByProduct(Product product);
}
