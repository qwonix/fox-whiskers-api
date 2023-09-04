package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.repository.DishRepository;
import ru.qwonix.foxwhiskersapi.repository.impl.mapper.DishDetailsRecordMapper;
import ru.qwonix.foxwhiskersapi.repository.impl.mapper.DishRecordMapper;
import ru.qwonix.foxwhiskersapi.repository.impl.mapper.DishTypeRecordMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class DishRepositoryImpl implements DishRepository {
    private final DSLContext dsl;

    private final DishRecordMapper dishRecordMapper = new DishRecordMapper();
    private final DishDetailsRecordMapper dishDetailsRecordMapper = new DishDetailsRecordMapper();
    private final DishTypeRecordMapper dishTypeRecordMapper = new DishTypeRecordMapper();

    public DishRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
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
    public Optional<Dish> find(Long id) {
        return dsl.selectFrom(Tables.DISH
                        .join(Tables.DISH_DETAILS)
                        .on(Tables.DISH.ID.eq(Tables.DISH_DETAILS.DISH_ID))
                        .join(Tables.DISH_TYPE)
                        .on(Tables.DISH.DISH_TYPE_ID.eq(Tables.DISH_TYPE.ID)))
                .where(Tables.DISH.ID.eq(id))
                .fetchOptional(record -> {
                    var dish = dishRecordMapper.map(record);
                    var dishDetails = dishDetailsRecordMapper.map(record);
                    var dishType = dishTypeRecordMapper.map(record);
                    dish.setDishDetails(dishDetails);
                    dish.setType(dishType);
                    return dish;
                });
    }

    @Override
    public List<Dish> findAll(Condition condition) {
        return dsl.selectFrom(Tables.DISH
                        .join(Tables.DISH_DETAILS)
                        .on(Tables.DISH.ID.eq(Tables.DISH_DETAILS.DISH_ID))
                        .join(Tables.DISH_TYPE)
                        .on(Tables.DISH.DISH_TYPE_ID.eq(Tables.DISH_TYPE.ID)))
                .where(condition)
                .fetch().map(record -> {
                    var dish = dishRecordMapper.map(record);
                    var dishDetails = dishDetailsRecordMapper.map(record);
                    var dishType = dishTypeRecordMapper.map(record);
                    dish.setDishDetails(dishDetails);
                    dish.setType(dishType);
                    return dish;
                });
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
