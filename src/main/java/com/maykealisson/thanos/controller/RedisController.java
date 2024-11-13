package com.maykealisson.thanos.controller;

import com.maykealisson.thanos.service.RedisService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@AllArgsConstructor
@RequestMapping(value = "/v1/redis")
public class RedisController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisController.class);

    private final RedisService redisService;

    @GetMapping(value = "/reativo")
    public ResponseEntity<Mono<String>> reativoComTimeout(){
        LOGGER.info("Iniciando fluxo redis reativo com timeout");
        var response = redisService.executeTimeout();
        var valor = response.map(result -> String.format("%s", result));
        return ResponseEntity.status(HttpStatus.OK).body(valor);
    }


    @PostMapping(value = "/reativo")
    public ResponseEntity<Mono<String>> reativo(){
        LOGGER.info("Iniciando fluxo redis reativo");
        var response = redisService.execute();
        var valor = response.map(result -> String.format("%s", result));
        return ResponseEntity.status(HttpStatus.CREATED).body(valor);
    }

    @PostMapping(value = "/reativo/notificacao")
    public ResponseEntity<Mono<String>> reativoPublich(){
        LOGGER.info("Iniciando fluxo redis reativo publicacao");
        redisService.publica();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
