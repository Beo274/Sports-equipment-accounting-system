package ru.etu.sport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.etu.sport.model.entity.EnumerationValue;

@Repository
public interface EnumerationValueRepository extends JpaRepository<EnumerationValue, Integer> {
    
    @Query(value = "SELECT * FROM enumeration_value WHERE enumeration_id = :enumerationId", nativeQuery = true)
    List<EnumerationValue> findByAtributeId(Integer enumerationId);
}
