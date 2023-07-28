package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;
import ru.qwonix.foxwhiskersapi.repository.PickUpLocationsRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class PickUpLocationsRepositoryImpl implements PickUpLocationsRepository {
    private final DSLContext dsl;

    public PickUpLocationsRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<PickUpLocation> findFirstByOrderByPriorityDesc() {
        return dsl.selectFrom(Tables.PICK_UP_LOCATION)
                .orderBy(Tables.PICK_UP_LOCATION.PRIORITY)
                .fetchOptionalInto(PickUpLocation.class);
    }

    @Override
    public PickUpLocation insert(PickUpLocation pickUpLocation) {
        return dsl.insertInto(Tables.PICK_UP_LOCATION)
                .set(dsl.newRecord(Tables.PICK_UP_LOCATION, pickUpLocation))
                .returning()
                .fetchOne()
                .into(PickUpLocation.class);
    }

    @Override
    public PickUpLocation update(PickUpLocation pickUpLocation) {
        return dsl.update(Tables.PICK_UP_LOCATION)
                .set(dsl.newRecord(Tables.PICK_UP_LOCATION, pickUpLocation))
                .returning()
                .fetchOne()
                .into(PickUpLocation.class);
    }

    @Override
    public PickUpLocation find(Long id) {
        return dsl.selectFrom(Tables.PICK_UP_LOCATION)
                .where(Tables.PICK_UP_LOCATION.ID.eq(id))
                .fetchAny()
                .into(PickUpLocation.class);
    }

    @Override
    public List<PickUpLocation> findAll(Condition condition) {
        return dsl.selectFrom(Tables.PICK_UP_LOCATION)
                .where(condition)
                .fetch()
                .into(PickUpLocation.class);
    }

    @Override
    public List<PickUpLocation> findAll() {
        return findAll(DSL.noCondition());
    }

    @Override
    public Boolean delete(Long id) {
        return dsl.deleteFrom(Tables.PICK_UP_LOCATION)
                .where(Tables.PICK_UP_LOCATION.ID.eq(id))
                .execute() == SUCCESS_CODE;
    }
}
