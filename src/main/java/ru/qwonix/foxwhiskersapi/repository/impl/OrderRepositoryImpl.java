package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.Order;
import ru.qwonix.foxwhiskersapi.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final DSLContext dsl;

    public OrderRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<Order> findAllByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public Order insert(Order order) {
        return dsl.insertInto(Tables.ORDER)
                .set(dsl.newRecord(Tables.ORDER, order))
                .returning()
                .fetchOne()
                .into(Order.class);
    }

    @Override
    public Order update(Order order) {
        return dsl.update(Tables.ORDER)
                .set(dsl.newRecord(Tables.ORDER, order))
                .returning()
                .fetchOne()
                .into(Order.class);
    }

    @Override
    public Optional<Order> find(Long id) {
        return dsl.selectFrom(Tables.ORDER)
                .where(Tables.ORDER.ID.eq(id))
                .fetchOptionalInto(Order.class);
    }

    @Override
    public List<Order> findAll(Condition condition) {
        return dsl.selectFrom(Tables.ORDER)
                .where(condition)
                .fetch()
                .into(Order.class);
    }

    @Override
    public List<Order> findAll() {
        return findAll(DSL.noCondition());
    }

    @Override
    public Boolean delete(Long id) {
        return dsl.delete(Tables.ORDER)
                       .where(Tables.ORDER.ID.eq(id))
                       .execute() == SUCCESS_CODE;
    }
}
