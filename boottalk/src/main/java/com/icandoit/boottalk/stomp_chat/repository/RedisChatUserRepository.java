package com.icandoit.boottalk.stomp_chat.repository;

import static com.icandoit.boottalk.common.RedisKeyPrefix.chatNotice;
import static com.icandoit.boottalk.common.RedisKeyPrefix.enteredStatus;

import java.time.Duration;
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

    public boolean shouldSendNotification(String roomUuid, Long receiverId) {

        Boolean alreadySent = redisTemplate.hasKey(chatNotice(roomUuid, receiverId));
        if (Boolean.TRUE.equals(alreadySent)) {
            return false; // 이미 알림 보냄
        }

        // 알림 전송 상태 저장 (커피챗이 진행되는 동안만 유지)
        redisTemplate.opsForValue().set(chatNotice(roomUuid, receiverId), true, Duration.ofMinutes(30));
        return true;
    }

    public void resetNotificationStatus(String roomUuid, Long receiverId) {
        redisTemplate.delete(chatNotice(roomUuid, receiverId));
    }
}