package com.icandoit.boottalk.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // 서버 연결 설정
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

    // Dto -> LDT 직렬화
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    // 채팅 메시지 복합 객체를 redis 저장 하기위해
    @Bean
    public RedisTemplate<String, Object> chatMessageDtoRedisTemplate(
        LettuceConnectionFactory redisConnectionFactory,
        GenericJackson2JsonRedisSerializer serializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

    // RedisChatRoomRepository 에서 사용중 위랑 병합 가능 여부 체크
    @Bean
    public RedisTemplate<String, ChatRoomResponseDto> chatRoomDtoRedisTemplate(
        LettuceConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, ChatRoomResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<ChatRoomResponseDto> serializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, ChatRoomResponseDto.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        return template;
    }

    @Bean
    public RedisTemplate<String, Boolean> booleanRedisTemplate(
        RedisConnectionFactory redisConnectionFactory,
        GenericJackson2JsonRedisSerializer serializer) {

        RedisTemplate<String, Boolean> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    // 직렬화를 위한 objectMapper 추가
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
