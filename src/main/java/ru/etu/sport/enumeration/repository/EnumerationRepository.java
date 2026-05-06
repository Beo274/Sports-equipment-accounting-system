package ru.etu.sport.enumeration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.model.entity.Enumeration;

@Repository
public interface EnumerationRepository extends JpaRepository<Enumeration, Integer> {

    public Enumeration getEnumerationById(Integer id);
}