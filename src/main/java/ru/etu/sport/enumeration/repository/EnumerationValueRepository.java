package ru.etu.sport.enumeration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.etu.sport.model.entity.EnumerationValue;

public interface EnumerationValueRepository extends JpaRepository<EnumerationValue, Integer> {
    
    @Query(value = "SELECT * FROM enumeration_value WHERE enumeration_id = :enumerationId", nativeQuery = true)
    List<EnumerationValue> findByAtributeId(Integer enumerationId);
}
