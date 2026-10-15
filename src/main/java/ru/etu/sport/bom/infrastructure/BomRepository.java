package ru.etu.sport.bom.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.bom.persistence.BomEntity;
import ru.etu.sport.model.entity.Product;

import java.util.List;

public interface BomRepository extends JpaRepository<BomEntity, Integer> {

    List<BomEntity> findAllByParentProduct(Product product);
}
