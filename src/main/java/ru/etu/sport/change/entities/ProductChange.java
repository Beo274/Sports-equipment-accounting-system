package ru.etu.sport.change.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.etu.sport.model.entity.Product;

import java.time.LocalDate;

@Entity
@Table(name = "product_change")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "change_date")
    private LocalDate date;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(name = "description")
    private String description;
}
