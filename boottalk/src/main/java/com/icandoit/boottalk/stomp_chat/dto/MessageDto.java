package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long senderId;
    private Long receiverId;
    private Long chatRoomId;
    private String message;

    public static MessageDto from(Message message) {
        return MessageDto.builder()
            .senderId(message.getSender().getUserId())
            .receiverId(message.getReceiver().getUserId())
            .chatRoomId(message.getChatRoom().getChatRoomId())
            .message(message.getMessage())
            .build();
    }
}
