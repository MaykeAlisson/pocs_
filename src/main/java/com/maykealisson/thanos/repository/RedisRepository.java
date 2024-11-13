package com.maykealisson.thanos.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public Mono<String> getValue(String key){
        return reactiveStringRedisTemplate.opsForValue().get(key);
    }

    public Mono<Void> setValue(String key, String value, Duration ttl){
        return reactiveStringRedisTemplate.opsForValue().set(key, value, ttl).then();
    }

    public Mono<Void> removeValue(String key){
        return reactiveStringRedisTemplate.opsForValue().delete(key).then();
    }

    public Mono<Void> convertPush(String to, String msg){
        return reactiveStringRedisTemplate.convertAndSend(to, msg).then();
    }

    public String getRegister(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }
}
