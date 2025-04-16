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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChatRoomStatusUpdater {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomStatusRepository statusRepository;
    private final ChatRoomRedisManager redisManager;

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

        // redis 유저 입장 정보 저장
        redisManager.addUserToRoom(userId, roomUuid);
        statusRepository.save(status);
    }

    public void updateChatRoomStatusOnLeave(Long userId) {

        // 유저가 속한 방 정보 조회
        String roomUuid = redisManager.findUserRoom(userId);

        if (roomUuid == null) {
            return;
        }

        ChatRoomStatus status = statusRepository.findByChatRoom_RoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_STATUS_NOT_FOUND));

        ChatRoom chatRoom = status.getChatRoom();

        // 유저가 멘토인지 멘티인지 확인하고 상태값 업데이트(입장 -> 퇴장)
        if (chatRoom.getMentor().getUserId().equals(userId)) {
            status.setMentorEntered(false);
        } else {
            status.setMenteeEntered(false);
        }

        log.info("User {} 퇴장 - Room: {}, mentorEntered: {}, menteeEntered: {}",
            userId, roomUuid, status.isMentorEntered(), status.isMenteeEntered());

        redisManager.removeUserFromRoom(userId, roomUuid);
    }
}