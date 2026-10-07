package ru.etu.sport.parameter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.ProductParameter;

import java.util.List;

@Repository
public interface ProductParameterRepository extends JpaRepository<ProductParameter, Integer> {
    List<ProductParameter> findByParameterId(Integer paramId);

    List<ProductParameter> findByProduct_ProductClass_Id(Integer classId);
    List<ProductParameter> findByParameter_IdAndIntValAndEnumerationValue_Id(Integer paramId, Integer intVal, Integer enumId);

    @Query("SELECT pp FROM ProductParameter pp " +
           "JOIN FETCH pp.parameter " +
           "JOIN FETCH pp.product p " +
           "LEFT JOIN FETCH pp.enumerationValue ev " +
           "WHERE pp.parameter.id IN :paramIds")
    List<ProductParameter> findByParamIds(@Param("paramIds") List<Integer> paramIds);

    @Query("SELECT pp FROM ProductParameter pp WHERE pp.product.id = :productId")
    List<ProductParameter> findByProductId(@Param("productId") Integer productId);

    @Query("SELECT DISTINCT pp.product.id FROM ProductParameter pp " +
            "WHERE pp.parameter.id = :paramId " +
            "AND (pp.intVal BETWEEN :minVal AND :maxVal OR pp.minVal <= :minVal AND pp.maxVal >= :maxVal)")
    List<Integer> findProductIdsByParamRange(
            @Param("paramId") Integer paramId,
            @Param("minVal") Integer minVal,
            @Param("maxVal") Integer maxVal
    );

    @Query("SELECT pp FROM ProductParameter pp " +
            "JOIN FETCH pp.product p " +
            "LEFT JOIN FETCH p.productClass " +
            "LEFT JOIN FETCH pp.enumerationValue " +
            "WHERE p.id IN :productIds")
    List<ProductParameter> findAllParamsForProducts(@Param("productIds") List<Integer> productIds);


    @Query("SELECT pp.product.id FROM ProductParameter pp " +
            "WHERE pp.parameter.id IN :paramIds " +
            "GROUP BY pp.product.id " +
            "HAVING COUNT(DISTINCT pp.parameter.id) = :paramCount")
    List<Integer> findProductIdsByAllParams(
            @Param("paramIds") List<Integer> paramIds,
            @Param("paramCount") Long paramCount
    );

}