package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.Message;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import java.time.LocalDateTime;

public record MessageResponseDto(
    String roomUuid,
    Long senderId,
    String senderName,
    String message,
    MessageType type,
    LocalDateTime sentAt
) {
    public static MessageResponseDto from(Message message) {
        return new MessageResponseDto(
            message.getRoomUuid(),
            message.getSenderId(),
            message.getSenderName(),
            message.getMessage(),
            message.getType(),
            message.getSentAt()
        );
    }
}