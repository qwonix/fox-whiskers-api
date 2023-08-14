package ru.qwonix.foxwhiskersapi.repository;

import org.jooq.Condition;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    Integer SUCCESS_CODE = 1;

    T insert(T t);

    T update(T t);

    Optional<T> find(ID id);

    List<T> findAll(Condition condition);

    List<T> findAll();

    Boolean delete(ID id);
}
