package com.icandoit.boottalk.stomp_chat.repository;

import static com.icandoit.boottalk.common.RedisKeyPrefix.chatMessages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisChatMessageRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // (LinkedHashMap -> DTO 변환용)

    public List<ChatMessageResponseDto> getMessages(String roomUuid) {
        return getMessagesForDto(roomUuid, 0, -1);
    }

    // 특정 채팅방의 메시지를 일부 배치로 가져오는 메서드
    public List<ChatMessageResponseDto> getMessagesForBatch(String roomUuid) {
        return getMessagesForDto(roomUuid, 0, 100);
    }

    // 단일 메시지 저장 + TTL 설정
    public void save(String roomUuid, ChatMessageResponseDto message, Duration ttl) {
        String key = chatMessages(roomUuid);
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, ttl);
    }

    public void saveAll(String roomUuid, List<ChatMessageResponseDto> messages, Duration ttl) {
        String key = chatMessages(roomUuid);

        redisTemplate.delete(key);
        for(ChatMessageResponseDto message : messages) {
            redisTemplate.opsForList().rightPush(key, message);
        }

        redisTemplate.expire(key, ttl);
    }

    // 메시지 일부 삭제 (리스트 앞 부분 자르기)
    public void deleteMessages(String roomUuid, int count) {
        redisTemplate.opsForList().trim(chatMessages(roomUuid), count, -1);
    }

    public List<ChatMessageResponseDto> getMessagesForDto(String key, long start, long end) {
        List<Object> cached = redisTemplate
            .opsForList()
            .range(chatMessages(key), start, end); //  List<Object>로 수정

        if (cached == null) {
            return Collections.emptyList();
        }

        return cached.stream()
            .map(o -> {
                if (o instanceof ChatMessageResponseDto) {
                    return (ChatMessageResponseDto) o;
                } else if (o instanceof Map) {
                    return objectMapper.convertValue(o, ChatMessageResponseDto.class); //  LinkedHashMap 변환
                } else {
                    throw new IllegalStateException("Unknown type from Redis: " + o.getClass());
                }
            })
            .toList();
    }
}