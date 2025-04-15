package com.icandoit.boottalk.stomp_chat.repository;

import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepository {

    private final RedisTemplate<String, ChatRoom> redisTemplate;
    private static final String CHAT_ROOM_KEY_PREFIX = "chat:room:";

    // 채팅방 정보 Redis에 저장
    public void save(ChatRoom chatRoom) {
        String key = CHAT_ROOM_KEY_PREFIX + chatRoom.getRoomUuid();
        redisTemplate.opsForValue().set(key, chatRoom, Duration.ofMinutes(30));
    }

    // Redis에서 채팅방 정보 조회
    public ChatRoom findById(String roomUuid) {
        String key = CHAT_ROOM_KEY_PREFIX + roomUuid;
        return redisTemplate.opsForValue().get(key);
    }
}