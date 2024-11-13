package com.maykealisson.thanos.service;

import com.maykealisson.thanos.event.RedisEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RedisService {

    private final RedisEvent redisEvent;

    public Mono<String> execute(){
        // Realiza o primeiro processamento

        var key = "anyChaveRedis";
        var tempoEmSegundos = 30L;

        Mono<String> monoChaveRedis = redisEvent.getMemory(key, tempoEmSegundos);

        return monoChaveRedis.map(this::processamento);

    }

    public Mono<String> executeTimeout(){
        // Realiza o primeiro processamento

        var key = "anyChaveRedis";

        Mono<String> monoChaveRedis = redisEvent.getMemoryTimeout(key, Duration.ofSeconds(30));

        return monoChaveRedis.map(this::processamento);
    }

    public void publica(){
        var key = "anyChaveRedis";
        var value = String.format("%s_%s", "notificacao", LocalDateTime.now());
        redisEvent.publish(key, value);
    }

    private String processamento(String chave){

        // Recupera registro no redis
        // Realiza processamento


        return "Processamento finalizado";
    }
}
