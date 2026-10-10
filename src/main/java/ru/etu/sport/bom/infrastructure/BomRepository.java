package ru.etu.sport.bom.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.bom.persistence.BomEntity;

public interface BomRepository extends JpaRepository<BomEntity, Integer> {
    
}
