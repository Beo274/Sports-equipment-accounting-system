package ru.etu.sport.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.etu.sport.model.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query("UPDATE Product p SET p.classId = :newParentId WHERE p.id =:id")
    void updateParentId(Integer id, Integer newParentId);

    @Query("SELECT p FROM Product p")
    List<Product> getProducts(Pageable pageable);

    List<Product> findByClassId(Integer classId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p")
    Integer countProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.classId = :classId")
    Integer countProducts(Integer classId);

    @Query("DELETE FROM Product p WHERE p.id = :id")
    void delete(Integer id);
}
