package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatMessageRepository {

    private final RedisTemplate<String, ChatMessageResponseDto> chatMessageDtoRedisTemplate;


    private static final String CHAT_MESSAGE_KEY_PREFIX = "chat:messages:";

    // 채팅 메시지 전체 조회
    public List<ChatMessageResponseDto> getMessages(String roomUuid) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;

        // Redis에서 DTO 타입으로 꺼내오기
        List<ChatMessageResponseDto> cached = chatMessageDtoRedisTemplate
            .opsForList()
            .range(key, 0, -1);

        return cached != null ? cached : Collections.emptyList();
    }

    // 단일 메시지 저장 + TTL 설정
    public void save(String roomUuid, ChatMessageResponseDto message, Duration ttl) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        chatMessageDtoRedisTemplate.opsForList().rightPush(key, message);
        chatMessageDtoRedisTemplate.expire(key, ttl);
    }

    public void saveAll(String roomUuid, List<ChatMessageResponseDto> messages, Duration ttl) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;

        for (ChatMessageResponseDto msg : messages) {
            chatMessageDtoRedisTemplate.opsForList().rightPush(key, msg);
        }
        chatMessageDtoRedisTemplate.expire(key, ttl);
    }

    // 메시지 일부 삭제 (리스트 앞 부분 자르기)
    public void deleteMessages(String roomUuid, int count) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        chatMessageDtoRedisTemplate.opsForList().trim(key, count, -1);
    }

    // 특정 채팅방의 메시지를 일부 배치로 가져오는 메서드
    public List<ChatMessageResponseDto> getMessagesForBatch(String roomUuid) {
        String key = CHAT_MESSAGE_KEY_PREFIX + roomUuid;
        List<ChatMessageResponseDto> cachedMessages = chatMessageDtoRedisTemplate.opsForList()
            .range(key, 0, 100);
        return cachedMessages != null ? cachedMessages : Collections.emptyList();
    }
}
