package ru.qwonix.foxwhiskersapi.repository.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.domain.Tables;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClientRepositoryImpl implements ClientRepository {
    private final DSLContext dsl;

    public ClientRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }


    @Override
    public Optional<Client> findByPhoneNumber(String phoneNumber) {
        return dsl.selectFrom(Tables.CLIENT)
                .where(Tables.CLIENT.PHONE_NUMBER.eq(phoneNumber))
                .fetchOptionalInto(Client.class);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return dsl.fetchExists(Tables.CLIENT, Tables.CLIENT.PHONE_NUMBER.eq(phoneNumber));
    }

    @Override
    public Client insert(Client client) {
        client.setId(UUID.randomUUID());
        return dsl.insertInto(Tables.CLIENT)
                .set(dsl.newRecord(Tables.CLIENT, client))
                .returning()
                .fetchOne()
                .into(Client.class);
    }

    @Override
    public Client update(Client client) {
        return dsl.update(Tables.CLIENT)
                .set(dsl.newRecord(Tables.CLIENT, client))
                .where(Tables.CLIENT.ID.eq(client.getId()))
                .returning()
                .fetchOne()
                .into(Client.class);
    }

    @Override
    public Optional<Client> find(UUID id) {
        return dsl.selectFrom(Tables.CLIENT)
                .where(Tables.CLIENT.ID.eq(id))
                .fetchOptionalInto(Client.class);
    }


    @Override
    public List<Client> findAll() {
        return findAll(DSL.noCondition());
    }

    @Override
    public List<Client> findAll(Condition condition) {
        return dsl.selectFrom(Tables.CLIENT)
                .where(condition)
                .fetch()
                .into(Client.class);
    }

    @Override
    public Boolean delete(UUID id) {
        return dsl.deleteFrom(Tables.CLIENT)
                       .where(Tables.CLIENT.ID.eq(id))
                       .execute() == SUCCESS_CODE;
    }
}
