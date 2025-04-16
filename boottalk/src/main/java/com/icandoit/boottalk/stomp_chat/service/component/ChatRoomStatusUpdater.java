package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoomStatus;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomStatusRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ChatRoomStatusUpdater {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomStatusRepository statusRepository;

    @Transactional
    public void updateChatRoomStatusOnEnter(Long userId, String roomUuid) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 예약 시간이 아직 안됐으면 예외 발생 (입장 불가)
        if (chatRoom.getReservationAt().isAfter(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_STARTED);
        }

        ChatRoomStatus status = chatRoom.getRoomStatus();

        if (Objects.equals(chatRoom.getMentor().getUserId(), userId)) {
            status.setMentorEntered(true);
        } else if (Objects.equals(chatRoom.getMentee().getUserId(), userId)) {
            status.setMenteeEntered(true);
        }

        status.setActive(true);
        statusRepository.save(status);
    }
}