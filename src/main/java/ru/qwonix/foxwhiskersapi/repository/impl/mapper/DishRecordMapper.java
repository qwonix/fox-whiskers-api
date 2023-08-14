package ru.qwonix.foxwhiskersapi.repository.impl.mapper;

import org.jooq.Record;
import org.jooq.RecordMapper;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.Dish;

public class DishRecordMapper implements RecordMapper<Record, Dish> {
    @Override
    public Dish map(Record record) {
        return Dish.builder()
                .id(record.get(Tables.DISH.ID))
                .title(record.get(Tables.DISH.TITLE))
                .currencyPrice(record.get(Tables.DISH.CURRENCY_PRICE))
                .isAvailable(record.get(Tables.DISH.IS_AVAILABLE))
                .build();
    }
}
