package ru.etu.sport.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.etu.sport.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
