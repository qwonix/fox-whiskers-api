package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.repository.DishRepository;

import java.util.List;
@Repository
public class DishRepositoryImpl implements DishRepository {
    private final DSLContext dsl;

    public DishRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<Dish> findByType_Id(Long id) {
        return dsl.selectFrom(Tables.DISH)
                .where(Tables.DISH.DISH_TYPE_ID.eq(id))
                .fetch()
                .into(Dish.class);
    }

    @Override
    public Dish insert(Dish dish) {
        return dsl.insertInto(Tables.DISH)
                .set(dsl.newRecord(Tables.DISH, dish))
                .returning()
                .fetchOne()
                .into(Dish.class);
    }

    @Override
    public Dish update(Dish dish) {
        return dsl.update(Tables.DISH)
                .set(dsl.newRecord(Tables.DISH, dish))
                .where(Tables.DISH.ID.eq(dish.getId()))
                .returning()
                .fetchOne()
                .into(Dish.class);
    }

    @Override
    public Dish find(Long id) {
        return dsl.selectFrom(Tables.DISH)
                .where(Tables.DISH.ID.eq(id))
                .fetchOne()
                .into(Dish.class);
    }

    @Override
    public List<Dish> findAll(Condition condition) {
        return dsl.selectFrom(Tables.DISH)
                .where(condition)
                .fetch()
                .into(Dish.class);
    }

    @Override
    public List<Dish> findAll() {
        return findAll(DSL.noCondition());
    }

    @Override
    public Boolean delete(Long id) {
        return dsl.deleteFrom(Tables.DISH)
                .where(Tables.DISH.ID.eq(id))
                .execute() == SUCCESS_CODE;
    }
}
