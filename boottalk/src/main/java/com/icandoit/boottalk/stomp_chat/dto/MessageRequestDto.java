package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;

public record MessageRequestDto(
    String roomId,
    Long senderId,
    Long receiverId,
    String content,
    MessageType type

) {

    public static MessageRequestDto from(String roomId, Long senderId, Long receiverId,
        String content, MessageType type) {
        return new MessageRequestDto(roomId, senderId, receiverId, content, type);
    }
}
