package com.icandoit.boottalk.stomp_chat.repository;

import static com.icandoit.boottalk.common.RedisKeyPrefix.*;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepository {

    private final RedisTemplate<String, ChatRoomResponseDto> redisTemplate;

    // 채팅방 정보 Redis에 저장
    public void saveChatRoomToCache(ChatRoomResponseDto chatRoomDto) {
        String key = roomInfo(chatRoomDto.roomUuid());
        log.info("Redis에 채팅방 저장 시도: {}", key);
        redisTemplate.opsForValue().set(key, chatRoomDto, Duration.ofMinutes(30));
    }

    // Redis에서 채팅방 정보 조회
    public ChatRoomResponseDto findChatRoomByRoomUuidFromCache(String roomUuid) {
        String key = roomInfo(roomUuid);
        return redisTemplate.opsForValue().get(key);
    }
}


