package com.icandoit.boottalk.stomp_chat.dto;

import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import java.time.LocalDateTime;

public record ChatRoomResponseDto(
    String roomUuid,
    String mentorName,
    Long mentorId,
    String menteeName,
    Long menteeId,
    LocalDateTime reservationAt,
    LocalDateTime expiresAt,
    boolean isActive
) {
    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
            chatRoom.getRoomUuid(),
            chatRoom.getMentor().getUserName(),
            chatRoom.getMentor().getUserId(),
            chatRoom.getMentee().getUserName(),
            chatRoom.getMentee().getUserId(),
            chatRoom.getReservationAt(),
            chatRoom.getEndAt(),
            chatRoom.isActive()
        );
    }
}