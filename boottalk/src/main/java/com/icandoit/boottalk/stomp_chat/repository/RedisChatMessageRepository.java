package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisChatMessageRepository {

    private final RedisTemplate<String, ChatMessageResponseDto> chatMessageRedisTemplate;

    public List<ChatMessageResponseDto> getMessages(String roomUuid) {
        return getMessagesForDto(roomUuid, 0, -1);
    }

    // 특정 채팅방의 메시지를 일부 배치로 가져오는 메서드
    public List<ChatMessageResponseDto> getMessagesForBatch(String roomUuid) {
        return getMessagesForDto(roomUuid, 0, 100);
    }

    // 단일 메시지 저장 + TTL 설정
    public void save(String roomUuid, ChatMessageResponseDto message) {
        String key = chatMessages(roomUuid);
        chatMessageRedisTemplate.opsForList().rightPush(key, message);
    }

    public void saveAll(String roomUuid, List<ChatMessageResponseDto> messages, Duration ttl) {
        String key = chatMessages(roomUuid);

        chatMessageRedisTemplate.delete(key);
        for (ChatMessageResponseDto message : messages) {
            chatMessageRedisTemplate.opsForList().rightPush(key, message);
        }

        chatMessageRedisTemplate.expire(key, ttl);
    }

    // 메시지 일부 삭제 (리스트 앞 부분 자르기)
    public void deleteMessages(String roomUuid, int count) {
        chatMessageRedisTemplate.opsForList().trim(chatMessages(roomUuid), count, -1);
    }

    public List<ChatMessageResponseDto> getMessagesForDto(String key, long start, long end) {
        List<ChatMessageResponseDto> cached = chatMessageRedisTemplate
            .opsForList()
            .range(chatMessages(key), start, end);

        return cached != null ? cached : Collections.emptyList();
    }

    private String chatMessages(String roomUuid) {
        return "chatMessages:" + roomUuid;
    }
}