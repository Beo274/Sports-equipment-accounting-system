package ru.etu.sport.order.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.order.model.Order;
import java.util.List;
import ru.etu.sport.organization.model.Organization;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrganization(Organization organization);
}
