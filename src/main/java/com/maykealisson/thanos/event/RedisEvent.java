package com.maykealisson.thanos.event;

import com.maykealisson.thanos.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class RedisEvent {

    private final RedisRepository redisRepository;
    private final ConcurrentMap<String, Sinks.One<String>> sinksMap = new ConcurrentHashMap<>();

    public Mono<String> getMemory(String key, Long timeOut, Integer lenth){
        Sinks.One<String> sink = Sinks.one();
        sinksMap.put(key, sink);

        try {
            return Mono.just(sink.asMono())
                    .toFuture()
                    .get(timeOut, TimeUnit.SECONDS);
        } catch (Exception e) {
            return Mono.just(String.format("%s_%s", key, lenth));
        } finally {
            sinksMap.remove(key);
        }
    }

    public Mono<String> getMemory(String key, Long timeOut){
        Sinks.One<String> sink = Sinks.one();
        sinksMap.put(key, sink);

        try {
            return Mono.just(sink.asMono())
                    .toFuture()
                    .get(timeOut, TimeUnit.SECONDS);
        } catch (Exception e) {
            return Mono.just(key);
        } finally {
            sinksMap.remove(key);
        }
    }

    public Mono<String> getMemoryTimeout(String key, Duration timeout){
        Sinks.One<String> sink = Sinks.one();
        sinksMap.put(key, sink);

        return sink.asMono()
                .timeout(timeout)
                .onErrorMap(TimeoutException.class, erro ->
                        new RuntimeException("Tempo da operacao exedido"))
                .doFinally(signalType -> sinksMap.remove(key));
    }

    public void publish(String key, String value){
        redisRepository.setValue(key, value, Duration.ofSeconds(60L))
                .then(redisRepository.convertPush("taskCompletion", key))
                .subscribe();
    }

    public void publishTimeout(String key, String value){
        redisRepository.setValue(key, value, Duration.ofSeconds(60L))
                .then(redisRepository.convertPush("taskCompletionLenth", key))
                .subscribe();
    }

    public void listnerMessage(String key){
        redisRepository.getValue(key)
                .flatMap(value -> {
                    Sinks.One<String> sink = sinksMap.get(key);
                    if (sink != null){
                        sink.tryEmitValue(value).orThrow();
                        return redisRepository.removeValue(key).then();
                    }
                    return Mono.empty();
                }).subscribe();
    }

    public void listnerMessageLenth(String key){
        redisRepository.getValue(key)
                .flatMap(value -> {
                    var split = key.split("_");
                    var keyNotLenth = split[0];
                    var lenth = Long.parseLong(split[1]);
                    Sinks.One<String> sink = sinksMap.get(key);
                    var notifications = 3L;
                    if (sink != null && Objects.equals(lenth, notifications)){
                        sink.tryEmitValue(value).orThrow();
                        return redisRepository.removeValue(keyNotLenth).then();
                    }
                    return Mono.empty();
                }).subscribe();
    }

}
