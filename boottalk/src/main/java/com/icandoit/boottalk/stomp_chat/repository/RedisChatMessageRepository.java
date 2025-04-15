package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatMessageRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_MESSAGE_KEY_PREFIX = "chat:messages:";

    public List<ChatMessage> getMessages(String roomUuid) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        List<Object> cached = redisTemplate.opsForList().range(key, 0, -1);
        if (cached == null) return Collections.emptyList();

        return cached.stream()
            .filter(obj -> obj instanceof ChatMessage)
            .map(obj -> (ChatMessage) obj)
            .toList();
    }

    public void save(String roomUuid, ChatMessage message, Duration ttl) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, ttl);
    }

    public void saveAll(String roomUuid, List<ChatMessage> messages, Duration ttl) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;

        for (ChatMessage msg : messages) {
            redisTemplate.opsForList().rightPush(key, msg);
        }

        redisTemplate.expire(key, ttl);
    }

    public void deleteMessages(List<ChatMessage> messages) {
        // 예시로 Redis에서 메시지를 삭제하는 코드
        String key = CHAT_MESSAGE_KEY_PREFIX + messages.get(0).getRoomUuid();
        redisTemplate.opsForList().trim(key, messages.size(), -1); // 삭제
    }


    public List<ChatMessage> getMessagesForBatch() {
        // Redis에 저장된 메시지를 일정량 가져오는 로직
        List<Object> cachedMessages = redisTemplate.opsForList().range("chat:messages", 0, 100);

        if (cachedMessages == null || cachedMessages.isEmpty()) {
            return Collections.emptyList();
        }

        // Object에서 ChatMessage로 변환
        return cachedMessages.stream()
            .filter(ChatMessage.class::isInstance) // ChatMessage 타입만 필터링
            .map(ChatMessage.class::cast) // ChatMessage로 형변환
            .collect(Collectors.toList());
    }
}
