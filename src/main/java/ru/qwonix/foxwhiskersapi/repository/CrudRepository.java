package ru.qwonix.foxwhiskersapi.repository;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.util.List;

public interface CrudRepository<T, ID> {

    Integer SUCCESS_CODE = 1;

    T insert(T t);

    T update(T t);

    T find(ID id);

    List<T> findAll(Condition condition);

    List<T> findAll();

    Boolean delete(ID id);
}
