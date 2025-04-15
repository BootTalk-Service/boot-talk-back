package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import java.time.LocalDateTime;

public record ChatRoomResponseDto(
    Long chatRoomId,
    String roomUuid,
    ChatUserDto mentor,
    ChatUserDto mentee,
    LocalDateTime reservationAt,
    LocalDateTime endAt,
    LocalDateTime expiresAt
) {

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
            chatRoom.getChatRoomId(),
            chatRoom.getRoomUuid(),
            ChatUserDto.from(chatRoom.getMentor()),
            ChatUserDto.from(chatRoom.getMentee()),
            chatRoom.getReservationAt(),
            chatRoom.getEndAt(),
            chatRoom.getExpiresAt()
        );
    }
}