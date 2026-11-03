package ru.etu.sport.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.operations.persistence.Profession;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {

}
