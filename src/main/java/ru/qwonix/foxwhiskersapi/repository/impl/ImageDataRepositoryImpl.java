package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
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
    public Optional<ImageData> findByImageName(String imageName) {
        return dsl.selectFrom(Tables.IMAGE_DATA)
                .where(Tables.IMAGE_DATA.FILE_NAME.eq(imageName))
                .fetchOptionalInto(ImageData.class);
    }

    @Override
    public Boolean exists(String imageName) {
        return dsl.fetchExists(
                dsl.selectFrom(Tables.IMAGE_DATA)
                        .where(Tables.IMAGE_DATA.FILE_NAME.eq(imageName)));

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
                .where(Tables.IMAGE_DATA.FILE_NAME.eq(imageData.getFileName()))
                .returning()
                .fetchOne()
                .into(ImageData.class);
    }

    @Override
    public Optional<ImageData> find(String imageName) {
        return dsl.selectFrom(Tables.IMAGE_DATA)
                .where(Tables.IMAGE_DATA.FILE_NAME.eq(imageName))
                .fetchOptionalInto(ImageData.class);
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
    public Boolean delete(String imageName) {
        return dsl.delete(Tables.IMAGE_DATA)
                       .where(Tables.IMAGE_DATA.FILE_NAME.eq(imageName))
                       .execute() == SUCCESS_CODE;
    }
}
