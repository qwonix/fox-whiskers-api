package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final DSLContext dsl;

    public UserRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }


    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return dsl.selectFrom(Tables.USER)
                .where(Tables.USER.PHONE_NUMBER.eq(phoneNumber))
                .fetchOptionalInto(User.class);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return dsl.fetchExists(Tables.USER, Tables.USER.PHONE_NUMBER.eq(phoneNumber));
    }

    @Override
    public User insert(User user) {
        user.setId(UUID.randomUUID());
        return dsl.insertInto(Tables.USER)
                .set(dsl.newRecord(Tables.USER, user))
                .returning()
                .fetchOne()
                .into(User.class);
    }

    @Override
    public User update(User user) {
        return dsl.update(Tables.USER)
                .set(dsl.newRecord(Tables.USER, user))
                .where(Tables.USER.ID.eq(user.getId()))
                .returning()
                .fetchOne()
                .into(User.class);
    }

    @Override
    public Optional<User> find(UUID id) {
        return dsl.selectFrom(Tables.USER)
                .where(Tables.USER.ID.eq(id))
                .fetchOptionalInto(User.class);
    }


    @Override
    public List<User> findAll() {
        return findAll(DSL.noCondition());
    }

    @Override
    public List<User> findAll(Condition condition) {
        return dsl.selectFrom(Tables.USER)
                .where(condition)
                .fetch()
                .into(User.class);
    }

    @Override
    public Boolean delete(UUID id) {
        return dsl.deleteFrom(Tables.USER)
                       .where(Tables.USER.ID.eq(id))
                       .execute() == SUCCESS_CODE;
    }
}
