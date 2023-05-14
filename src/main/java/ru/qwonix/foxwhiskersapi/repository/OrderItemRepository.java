package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.qwonix.foxwhiskersapi.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
