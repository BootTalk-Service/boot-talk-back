package com.icandoit.boottalk.stomp_chat.dto.stompDto;

public record ChatTypingResponseDto(
    Long receiverId,
    boolean typing
) {}