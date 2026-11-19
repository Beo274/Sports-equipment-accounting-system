package ru.etu.sport.order.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.etu.sport.order.infra.OrderItemRepository;
import ru.etu.sport.organization.model.Organization;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    OrderStatus status = OrderStatus.CREATED;

    @JoinColumn(name = "organization_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    Organization organization;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ", nullable = false, updatable = false)
    @CreationTimestamp
    ZonedDateTime createdAt;

    public OrderItem addItem(OrderItem item) {
        for (OrderItem orderItem : items) {
            if (orderItem.getProduct().getId() == item.getProduct().getId()) {
                orderItem.setQuantity(orderItem.getQuantity() + item.getQuantity());
                orderItem.setUnitPrice(item.getUnitPrice());

                return orderItem;
            }
        }

        items.add(item);
        item.setOrder(this);

        reindexItems();

        return item;
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        reindexItems();
    }

    public void removeItem(OrderItem item, OrderItemRepository repository) {
        items.remove(item);
        repository.delete(item);
        reindexItems();
    }

    public void removeItem(Long position, OrderItemRepository repository) {
        OrderItem item = items.remove(position.intValue() - 1);
        repository.delete(item);
        reindexItems();
    }

    public void removeItem(Long position) {
        items.remove(position.intValue() - 1);
        reindexItems();
    }

    public OrderItem getItem(Long position) {
        return items.get(position.intValue() - 1);
    }

    private void reindexItems() {
        for (Integer i = 0; i < items.size(); i++) {
            items.get(i).setPositionNumber(i.longValue() + 1);
        }
    }
}
