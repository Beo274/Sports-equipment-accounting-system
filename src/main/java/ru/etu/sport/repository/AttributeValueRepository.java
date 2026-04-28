package ru.etu.sport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.etu.sport.model.entity.AttributeValue;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Integer> {
    
    @Query(value = "SELECT * FROM attribute_value WHERE attribute_id = :attributeId", nativeQuery = true)
    List<AttributeValue> findByAtributeId(Integer attributeId);
}
