package com.icandoit.boottalk.stomp_chat.repository;

import static com.icandoit.boottalk.common.RedisKeyPrefix.roomInfo;

import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepository {

    private final RedisTemplate<String, ChatRoomResponseDto> redisTemplate;

    // 채팅방 정보 Redis에 저장
    public void saveChatRoomToCache(ChatRoomResponseDto chatRoomDto) {
        String key = roomInfo(chatRoomDto.roomUuid());
        redisTemplate.opsForValue().set(key, chatRoomDto, Duration.ofMinutes(30));
    }

    // Redis에서 채팅방 정보 조회
    public ChatRoomResponseDto findChatRoomByRoomUuidFromCache(String roomUuid) {
        String key = roomInfo(roomUuid);
        return redisTemplate.opsForValue().get(key);
    }
}


