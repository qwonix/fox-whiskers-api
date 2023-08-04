package ru.qwonix.foxwhiskersapi.repository;

import java.time.Duration;

public interface RedisRepository {

    Boolean hasKeyAndValue(String key, String value);

    void add(String key, String value, Duration duration);

    Boolean delete(String refreshToken);
}
