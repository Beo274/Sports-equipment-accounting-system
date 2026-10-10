package ru.etu.sport.bom.persistence;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.etu.sport.model.entity.Measure;
import ru.etu.sport.model.entity.Product;

@Entity
@Table(name = "bom")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn (name = "parent_product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Product parentProduct;

    @JoinColumn (name = "component_id", nullable = false)
    @ManyToOne (fetch = FetchType.LAZY)
    private Product component;

    @Column(
        check = @CheckConstraint(constraint = "quantity > 0", name = "check_quantity"), 
        nullable = false
    )
    private Integer quantity;

    @Column (
        name = "for_quantity", 
        check = @CheckConstraint(constraint = "for_quantity > 0", name = "check_for_quantity"), 
        nullable = false
    )
    private Integer forQuantity;

    @JoinColumn (name = "measure_unit_id")
    @ManyToOne (fetch = FetchType.LAZY)
    private Measure measure;

    @Column (name = "is_base")
    private Boolean isBase;
}
