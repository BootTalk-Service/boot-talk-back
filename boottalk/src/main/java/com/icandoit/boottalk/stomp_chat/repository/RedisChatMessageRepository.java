package com.icandoit.boottalk.stomp_chat.repository;

import static com.icandoit.boottalk.stomp_chat.common.RedisKeyPrefix.chatMessages;

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

    public List<ChatMessageResponseDto> getMessages(String roomUuid) {
        List<ChatMessageResponseDto> cached = chatMessageDtoRedisTemplate
            .opsForList()
            .range(chatMessages(roomUuid), 0, -1);

        return cached != null ? cached : Collections.emptyList();
    }

    // 단일 메시지 저장 + TTL 설정
    public void save(String roomUuid, ChatMessageResponseDto message, Duration ttl) {
        String key = chatMessages(roomUuid);
        chatMessageDtoRedisTemplate.opsForList().rightPush(key, message);
        chatMessageDtoRedisTemplate.expire(key, ttl);
    }

    public void saveAll(String roomUuid, List<ChatMessageResponseDto> messages, Duration ttl) {
        String key = chatMessages(roomUuid);
        for (ChatMessageResponseDto msg : messages) {
            chatMessageDtoRedisTemplate.opsForList().rightPush(key, msg);
        }
        chatMessageDtoRedisTemplate.expire(key, ttl);
    }

    // 메시지 일부 삭제 (리스트 앞 부분 자르기)
    public void deleteMessages(String roomUuid, int count) {
        chatMessageDtoRedisTemplate.opsForList().trim(chatMessages(roomUuid), count, -1);
    }

    // 특정 채팅방의 메시지를 일부 배치로 가져오는 메서드
    public List<ChatMessageResponseDto> getMessagesForBatch(String roomUuid) {
        List<ChatMessageResponseDto> cachedMessages = chatMessageDtoRedisTemplate
            .opsForList()
            .range(chatMessages(roomUuid), 0, 100);

        return cachedMessages != null ? cachedMessages : Collections.emptyList();
    }
}
