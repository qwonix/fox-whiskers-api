package ru.qwonix.foxwhiskersapi.repository;


import ru.qwonix.foxwhiskersapi.entity.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findAllByPhoneNumber(String phoneNumber);
}
