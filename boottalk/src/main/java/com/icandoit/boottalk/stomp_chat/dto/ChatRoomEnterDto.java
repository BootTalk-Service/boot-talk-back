package com.icandoit.boottalk.stomp_chat.dto;


public record ChatRoomEnterDto(
    String chatRoomUuid,
    Long enterUserId
) {

}