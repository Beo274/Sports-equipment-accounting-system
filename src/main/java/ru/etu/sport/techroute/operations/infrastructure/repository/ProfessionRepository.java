package ru.etu.sport.techroute.operations.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.techroute.operations.persistence.Profession;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {

}
