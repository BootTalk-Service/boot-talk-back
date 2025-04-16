package com.icandoit.boottalk.stomp_chat.dto.stompDto;

import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;

public record ChatMessageRequestDto(
    String roomUuid,
    Long senderId,
    String senderName,
    Long receiverId,
    String content,
    MessageType type
) {}