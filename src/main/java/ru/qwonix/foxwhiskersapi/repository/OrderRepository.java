package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.entity.Order;

import java.util.List;
import java.util.UUID;

@Repository

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByClientPhoneNumber(String phoneNumber);
}
