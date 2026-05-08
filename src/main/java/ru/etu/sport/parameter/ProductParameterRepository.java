package ru.etu.sport.parameter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.ProductParameter;
import java.util.List;

@Repository
public interface ProductParameterRepository extends JpaRepository<ProductParameter, Integer> {
    List<ProductParameter> findByProduct_ProductClass_Id(Integer classId);
    List<ProductParameter> findByParameter_IdAndIntValAndEnumerationValue_Id(Integer paramId, Integer intVal, Integer enumId);
}