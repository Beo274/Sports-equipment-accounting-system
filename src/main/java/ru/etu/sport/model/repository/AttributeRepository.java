package ru.etu.sport.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.etu.sport.model.entity.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, Integer> {

}
