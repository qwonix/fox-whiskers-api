package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.repository.ImageDataRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class ImageDataRepositoryImpl implements ImageDataRepository {

    private final DSLContext dsl;

    public ImageDataRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<ImageData> findByOriginalFileName(String originalFileName) {
        return dsl.selectFrom(Tables.IMAGE_DATA)
                .where(Tables.IMAGE_DATA.ORIGINAL_FILE_NAME.eq(originalFileName))
                .fetchOptionalInto(ImageData.class);
    }

    @Override
    public ImageData insert(ImageData imageData) {
        return dsl.insertInto(Tables.IMAGE_DATA)
                .set(dsl.newRecord(Tables.IMAGE_DATA, imageData))
                .returning()
                .fetchOne()
                .into(ImageData.class);
    }

    @Override
    public ImageData update(ImageData imageData) {
        return dsl.update(Tables.IMAGE_DATA)
                .set(dsl.newRecord(Tables.IMAGE_DATA, imageData))
                .where(Tables.IMAGE_DATA.ID.eq(imageData.getId()))
                .returning()
                .fetchOne()
                .into(ImageData.class);
    }

    @Override
    public ImageData find(Long id) {
        return dsl.selectFrom(Tables.IMAGE_DATA)
                .where(Tables.IMAGE_DATA.ID.eq(id))
                .fetchAny()
                .into(ImageData.class);
    }

    @Override
    public List<ImageData> findAll(Condition condition) {
        return dsl.selectFrom(Tables.IMAGE_DATA)
                .where(condition)
                .fetch()
                .into(ImageData.class);
    }

    @Override
    public List<ImageData> findAll() {
        return findAll(DSL.noCondition());
    }

    @Override
    public Boolean delete(Long id) {
        return dsl.delete(Tables.IMAGE_DATA)
                .where(Tables.IMAGE_DATA.ID.eq(id))
                .execute() == SUCCESS_CODE;
    }
}
