package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import java.time.LocalDateTime;

public record MessageResponseDto(
    String roomUuid,
    Long senderId,
    Long receiverId,
    String message,
    MessageType type,
    LocalDateTime sentAt
) {

    public static MessageResponseDto from(ChatMessage chatMessage) {
        return new MessageResponseDto(
            chatMessage.getRoomUuid(),
            chatMessage.getSenderId(),
            chatMessage.getReceiverId(),
            chatMessage.getContent(),
            chatMessage.getType(),
            chatMessage.getSentAt()
        );
    }
}