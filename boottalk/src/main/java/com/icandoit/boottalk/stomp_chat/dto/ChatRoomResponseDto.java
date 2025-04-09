package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import java.time.LocalDateTime;

public record ChatRoomResponseDto(
    String roomUuid,
    String mentorName,
    String menteeName,
    LocalDateTime reservationAt,
    LocalDateTime expiresAt
) {
    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
            chatRoom.getRoomUuid(),
            chatRoom.getMentor().getUserName(),
            chatRoom.getMentee().getUserName(),
            chatRoom.getReservationAt(),
            chatRoom.getEndAt()
        );
    }
}