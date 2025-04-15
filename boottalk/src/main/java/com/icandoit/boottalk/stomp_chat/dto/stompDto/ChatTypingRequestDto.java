package com.icandoit.boottalk.stomp_chat.dto.stompDto;

public record ChatTypingRequestDto(
    String roomUuid,
    Long receiverId,
    boolean typing
) {

}
