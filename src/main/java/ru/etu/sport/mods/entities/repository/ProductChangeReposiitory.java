package ru.etu.sport.mods.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.etu.sport.mods.entities.ProductChange;

@Repository
public interface ProductChangeReposiitory extends JpaRepository<ProductChange, Long> {
}
