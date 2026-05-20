package ru.etu.sport.product;

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
    @Query(value = "UPDATE product SET class_id = :newParentId WHERE id =:id", nativeQuery = true)
    void updateParentId(Integer id, Integer newParentId);

    @Query("SELECT p FROM Product p")
    List<Product> getProducts(Pageable pageable);

    @Query(value = "SELECT * FROM product WHERE class_id = :classId", nativeQuery = true)
    List<Product> findByClassId(Integer classId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p")
    Integer countProducts();

    @Query(value = "SELECT COUNT(*) FROM product WHERE class_id = :classId", nativeQuery = true)
    Integer countProducts(Integer classId);

    @Query("DELETE FROM Product p WHERE p.id = :id")
    @Modifying
    void delete(Integer id);

    List<Product> findByProductClass_IdIn(List<Integer> classIds);
}