package com.icandoit.boottalk.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config =
            new RedisStandaloneConfiguration(redisHost, redisPort);

        return new LettuceConnectionFactory(config);
    }

    // 키, 벨류를 저장 String 변환 저장 -> Spring 스케줄러에서 사용
    @Bean
    public StringRedisTemplate stringRedisTemplate(
        LettuceConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    // 직렬화를 위한 objectMapper 추가
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    // 채팅 메시지 전용 RedisTemplate
    @Bean
    public RedisTemplate<String, ChatMessageResponseDto> chatMessageRedisTemplate(
        LettuceConnectionFactory redisConnectionFactory,
        ObjectMapper objectMapper) {

        RedisTemplate<String, ChatMessageResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<ChatMessageResponseDto> serializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, ChatMessageResponseDto.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

    // 채팅방 전용 RedisTemplate
    @Bean
    public RedisTemplate<String, ChatRoomResponseDto> chatRoomRedisTemplate(
        LettuceConnectionFactory redisConnectionFactory,
        ObjectMapper objectMapper) {

        RedisTemplate<String, ChatRoomResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<ChatRoomResponseDto> serializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, ChatRoomResponseDto.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

    // Boolean 값 전용 RedisTemplate
    @Bean
    public RedisTemplate<String, Boolean> booleanRedisTemplate(
        RedisConnectionFactory redisConnectionFactory,
        ObjectMapper objectMapper) {

        RedisTemplate<String, Boolean> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<Boolean> serializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, Boolean.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}