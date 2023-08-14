package ru.qwonix.foxwhiskersapi.repository.impl.mapper;

import org.jooq.Record;
import org.jooq.RecordMapper;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.DishDetails;

public class DishDetailsRecordMapper implements RecordMapper<Record, DishDetails> {
    @Override
    public DishDetails map(Record record) {
        return DishDetails.builder()
                .compositionText(record.get(Tables.DISH_DETAILS.COMPOSITION_TEXT))
                .measureText(record.get(Tables.DISH_DETAILS.MEASURE_TEXT))
                .imageName(record.get(Tables.DISH_DETAILS.IMAGE_NAME))
                .build();
    }
}
