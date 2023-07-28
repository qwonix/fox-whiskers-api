package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.repository.OrderItemRepository;


@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {
    private final DSLContext dsl;

    public OrderItemRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }


}
