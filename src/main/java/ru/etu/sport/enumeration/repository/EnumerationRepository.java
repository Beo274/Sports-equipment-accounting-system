package ru.etu.sport.enumeration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.etu.sport.model.entity.Enumeration;

public interface EnumerationRepository extends JpaRepository<Enumeration, Integer> {

    public Enumeration getEnumerationById(Integer id);
}