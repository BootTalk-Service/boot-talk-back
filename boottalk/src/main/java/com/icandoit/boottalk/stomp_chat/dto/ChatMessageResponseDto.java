package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import java.time.LocalDateTime;

public record ChatMessageResponseDto(
    String roomUuid,
    Long senderId,
    Long receiverId,
    String message,
    MessageType type,
    LocalDateTime sentAt,
    boolean isRead
) {

    public static ChatMessageResponseDto from(ChatMessage chatMessage) {
        return new ChatMessageResponseDto(
            chatMessage.getRoomUuid(),
            chatMessage.getSenderId(),
            chatMessage.getReceiverId(),
            chatMessage.getContent(),
            chatMessage.getType(),
            chatMessage.getSentAt(),
            chatMessage.isRead()
        );
    }

    public ChatMessage toEntity() {
        return ChatMessage.builder()
            .roomUuid(roomUuid)
            .senderId(senderId)
            .receiverId(receiverId)
            .content(message)
            .isRead(isRead)
            .type(type)
            .sentAt(sentAt)
            .build();
    }
}