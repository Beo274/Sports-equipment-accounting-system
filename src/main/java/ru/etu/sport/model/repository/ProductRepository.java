package ru.etu.sport.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.etu.sport.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query("UPDATE Product p SET p.classId = :newParentId WHERE p.id =:id")
    void updateParentId(Integer id, Integer newParentId);
}
