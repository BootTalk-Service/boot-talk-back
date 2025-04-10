package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;

public record MessageRequestDto(
    String roomUuid,
    Long senderId,
    String senderName,
    Long receiverId,
    String message,
    MessageType type
) {}