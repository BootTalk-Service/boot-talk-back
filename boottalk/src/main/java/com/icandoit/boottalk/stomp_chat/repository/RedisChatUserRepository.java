package com.icandoit.boottalk.stomp_chat.repository;

import static com.icandoit.boottalk.common.RedisKeyPrefix.chatNotice;
import static com.icandoit.boottalk.common.RedisKeyPrefix.enteredStatus;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisChatUserRepository {

    private final RedisTemplate<String, Boolean> redisTemplate;


    // 사용자 입장 여부 체크
    public boolean isUserEnteredInCache(String roomUuid, Long userId) {
        return redisTemplate.opsForValue().get(enteredStatus(roomUuid, userId)) != null;
    }

    // 사용자의 입장 여부 저장
    public void saveUserEnterStatus(String roomUuid, Long userId) {
        redisTemplate.opsForValue().set(enteredStatus(roomUuid, userId), true);
    }


    // 사용자 알림 수신
    public boolean isNotificationNecessaryAndSend(String roomUuid, Long receiverId) {
        String key = chatNotice(roomUuid, receiverId);

        try {
            Boolean alreadySent = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(alreadySent)) {
                log.info("이미 알림이 전송됨: roomUuid={}, receiverId={}", roomUuid, receiverId);
                return false; // 이미 알림을 보냄
            }

            // 알림 전송 상태 저장 (커피챗이 진행되는 동안만 유지)
            redisTemplate.opsForValue().set(key, true, Duration.ofMinutes(30));
            return true;

        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패: roomUuid={}, receiverId={}", roomUuid, receiverId, e);
            return true; // Redis 장애 시 알림 전송 허용
        } catch (Exception e) {
            log.error("알림 상태 확인 중 오류 발생: roomUuid={}, receiverId={}", roomUuid, receiverId, e);
            return true; // 오류 발생 시 안전하게 알림 전송 허용
        }
    }

    // 사용자 수신 상태 제거
    public void resetNotificationStatus(String roomUuid, Long receiverId) {
        redisTemplate.delete(chatNotice(roomUuid, receiverId));
    }
}


