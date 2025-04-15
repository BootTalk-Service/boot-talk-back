package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatMessageRepository {

    private final RedisTemplate<String, ChatMessage> redisTemplate;

    private static final String CHAT_MESSAGE_KEY_PREFIX = "chat:messages:";

    // 채팅 메시지 전체 조회
    public List<ChatMessage> getMessages(String roomUuid) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        List<ChatMessage> cached = redisTemplate.opsForList().range(key, 0, -1);
        return cached != null ? cached : Collections.emptyList();
    }

    // 단일 메시지 저장 + TTL 설정
    public void save(String roomUuid, ChatMessage message, Duration ttl) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, ttl);
    }

    // 여러 메시지 저장 + TTL 설정
    public void saveAll(String roomUuid, List<ChatMessage> messages, Duration ttl) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;

        for (ChatMessage msg : messages) {
            redisTemplate.opsForList().rightPush(key, msg);
        }
        redisTemplate.expire(key, ttl);
    }

    // 메시지 일부 삭제 (리스트 앞 부분 자르기)
    public void deleteMessages(List<ChatMessage> messages) {
        String key = CHAT_MESSAGE_KEY_PREFIX + messages.get(0).getRoomUuid();
        redisTemplate.opsForList().trim(key, messages.size(), -1);
    }

    // 특정 채팅방의 메시지를 일부 배치로 가져오는 메서드
    public List<ChatMessage> getMessagesForBatch(String roomUuid) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        List<ChatMessage> cachedMessages = redisTemplate.opsForList().range(key, 0, 100);
        return cachedMessages != null ? cachedMessages : Collections.emptyList();
    }
}
