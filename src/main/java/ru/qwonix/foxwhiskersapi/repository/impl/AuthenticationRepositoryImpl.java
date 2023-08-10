package ru.qwonix.foxwhiskersapi.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class AuthenticationRepositoryImpl implements AuthenticationRepository {

    private final RedisTemplate<String, String> redisTemplate;


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
