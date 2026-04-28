package ru.etu.sport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.Attribute;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Integer> {

    public Attribute getAttributeById(Integer id);
}