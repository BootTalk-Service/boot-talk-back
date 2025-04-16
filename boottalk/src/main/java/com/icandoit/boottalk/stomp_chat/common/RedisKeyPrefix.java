package com.icandoit.boottalk.stomp_chat.common;

public class RedisKeyPrefix {
    // Prefix constants
    private static final String ROOM_USERS_PREFIX = "chat:room:users:";
    private static final String USER_ROOM_PREFIX = "chat:user:room:";
    private static final String CHAT_MESSAGES_PREFIX = "chat:messages:";
    private static final String ROOM_INFO_PREFIX = "chat:room:info:";
    private static final String ENTERED_STATUS_PREFIX = "chat:entered:";

    // 채팅방에 속한 유저들 (Set)
    public static String roomUsers(String roomUuid) {
        return ROOM_USERS_PREFIX + roomUuid;
    }

    // 유저가 속한 방
    public static String userRoom(Long userId) {
        return USER_ROOM_PREFIX + userId;
    }

    // 채팅 메시지 저장
    public static String chatMessages(String roomUuid) {
        return CHAT_MESSAGES_PREFIX + roomUuid;
    }

    // 채팅방 정보 저장
    public static String roomInfo(String roomUuid) {
        return ROOM_INFO_PREFIX + roomUuid;
    }

    // 사용자의 입장 상태 저장
    public static String enteredStatus(String roomUuid, Long userId) {
        return ENTERED_STATUS_PREFIX + roomUuid + ":" + userId;
    }
}
