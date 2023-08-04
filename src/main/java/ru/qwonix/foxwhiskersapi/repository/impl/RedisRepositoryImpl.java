package ru.qwonix.foxwhiskersapi.repository.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.repository.RedisRepository;

import java.time.Duration;

@Repository
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Boolean hasKeyAndValue(String key, String value) {
        return redisTemplate.opsForValue().get(key).equals(value);
    }

    @Override
    public void add(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    @Override
    public Boolean delete(String refreshToken) {
        return redisTemplate.delete(refreshToken);
    }
}
