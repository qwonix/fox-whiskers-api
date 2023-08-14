package ru.qwonix.foxwhiskersapi.repository.impl.mapper;

import org.jooq.Record;
import org.jooq.RecordMapper;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.DishType;

public class DishTypeRecordMapper implements RecordMapper<Record, DishType> {
    @Override
    public DishType map(Record record) {
        return DishType.builder()
                .id(record.get(Tables.DISH_TYPE.ID))
                .title(record.get(Tables.DISH_TYPE.TITLE))
                .isAvailable(record.get(Tables.DISH_TYPE.IS_AVAILABLE))
                .build();
    }
}
