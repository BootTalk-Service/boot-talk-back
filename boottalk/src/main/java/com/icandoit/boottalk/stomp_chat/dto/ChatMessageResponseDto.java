package com.icandoit.boottalk.stomp_chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatMessageResponseDto {

    private String roomUuid;
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageType type;
    private LocalDateTime sentAt;

    @JsonProperty("read")
    private boolean isRead;

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
            .content(content)
            .isRead(isRead)
            .type(type)
            .sentAt(sentAt)
            .build();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}