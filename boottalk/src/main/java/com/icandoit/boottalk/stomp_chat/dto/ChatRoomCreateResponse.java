package com.icandoit.boottalk.stomp_chat.dto;

public record ChatRoomCreateResponse(
    String roomUuid
) {

    public static ChatRoomCreateResponse from(String roomUuid) {
        return new ChatRoomCreateResponse(roomUuid);
    }
}
