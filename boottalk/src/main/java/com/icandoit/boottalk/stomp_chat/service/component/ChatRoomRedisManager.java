package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.stomp_chat.common.RedisKeyPrefix;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomRedisManager {

    private final RedisTemplate<String, String> redisTemplate;


     // 방에 유저 추가 (Set) -> 유저 → 방 매핑 저장 (Value)
    public void addUserToRoom(Long userId, String roomUuid) {
        redisTemplate.opsForSet().add(RedisKeyPrefix.roomUsers(roomUuid), String.valueOf(userId));
        redisTemplate.opsForValue().set(RedisKeyPrefix.userRoom(userId), roomUuid);
    }


    // 방 Set에서 유저 제거 -> 유저 → 방 매핑 삭제
    public void removeUserFromRoom(Long userId, String roomUuid) {
        redisTemplate.opsForSet()
            .remove(RedisKeyPrefix.roomUsers(roomUuid), String.valueOf(userId));
        redisTemplate.delete(RedisKeyPrefix.userRoom(userId));
    }


     // 유저가 현재 속한 채팅방 UUID 조회
    public String findUserRoom(Long userId) {
        return redisTemplate.opsForValue().get(RedisKeyPrefix.userRoom(userId));
    }
}
