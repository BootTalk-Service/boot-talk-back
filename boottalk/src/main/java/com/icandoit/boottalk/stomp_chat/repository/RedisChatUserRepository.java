package com.icandoit.boottalk.stomp_chat.repository;

import static com.icandoit.boottalk.stomp_chat.common.RedisKeyPrefix.enteredStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisChatUserRepository {

    private final RedisTemplate<String, Boolean> redisTemplate;

    // 사용자 입장 여부 체크
    public boolean hasUserEntered(String roomUuid, Long userId) {
        return redisTemplate.opsForValue().get(enteredStatus(roomUuid, userId)) != null;
    }

    // 사용자의 입장 여부 저장
    public void saveUserEnterStatus(String roomUuid, Long userId) {
        redisTemplate.opsForValue().set(enteredStatus(roomUuid, userId), true);
    }
}