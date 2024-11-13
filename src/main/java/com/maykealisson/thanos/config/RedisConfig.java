package com.maykealisson.thanos.config;


import com.maykealisson.thanos.event.RedisEvent;
import com.maykealisson.thanos.model.RedisCredenciaisModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.data.redis.RedisReactiveHealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.url}")
    private String url;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(){
        var redisCredenciais = new RedisCredenciaisModel(url);
        var url = redisCredenciais.getUrl();
        var porta = redisCredenciais.getPorta();
        var configuracao = new RedisStandaloneConfiguration(url, porta);
        configuracao.setPassword(redisCredenciais.getSenha());

        var clientConfig = LettuceClientConfiguration.builder();
        if(redisCredenciais.getSsl()){
            clientConfig = clientConfig.useSsl().disablePeerVerification().and();
        }
        clientConfig.commandTimeout(Duration.ofSeconds(60L)).shutdownTimeout(Duration.ZERO);
        return new LettuceConnectionFactory(configuracao, clientConfig.build());
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(){
        return new ReactiveStringRedisTemplate(this.reactiveRedisConnectionFactory());
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisHealthIndicator")
    public ReactiveHealthIndicator reactiveHealthIndicator(){
        return new RedisReactiveHealthIndicator(this.reactiveRedisConnectionFactory());
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic("taskCompletion"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisEvent redisEvent){
        return new MessageListenerAdapter(redisEvent, "listnerMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerTimeOutContainer(RedisConnectionFactory connectionFactory,
                                                                              MessageListenerAdapter listenerAdapterTimeout){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapterTimeout, new ChannelTopic("taskCompletionLenth"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapterTimeout(RedisEvent redisEvent){
        return new MessageListenerAdapter(redisEvent, "listnerMessageLenth");
    }

}
