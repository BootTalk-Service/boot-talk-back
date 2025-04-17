package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatUserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChatMessageSender {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatUserRepository redisChatUserRepository;
    private final SimpMessageSendingOperations template;


    // 입장 메시지 전송
    public void sendEnterMessage(Long userId, String roomUuid) {
        // Redis에서 사용자가 입장했는지 확인
        if (!redisChatUserRepository.hasUserEntered(roomUuid, userId)) {
            return;
        }
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 상대방 ID 찾기
        Long receiverId = chatRoom.getMentor().getUserId().equals(userId)
            ? chatRoom.getMentee().getUserId()
            : chatRoom.getMentor().getUserId();

        ChatMessageResponseDto enterMessage = new ChatMessageResponseDto(
            roomUuid, 0L, receiverId, "님이 입장하였습니다.", MessageType.SYSTEM, LocalDateTime.now(), false
        );

        // 공통화된 메시지 전송 메서드 사용
        sendMessage(receiverId, enterMessage);
    }

    // WebSocket으로 전송하는 공통 로직
    public void sendMessage(Long receiverId, ChatMessageResponseDto chatMessageResponseDto) {
        String roomUuid = chatMessageResponseDto.getRoomUuid();

        String destination = "/queue/chat/" + roomUuid + "/" + receiverId;
        template.convertAndSend(destination, chatMessageResponseDto);

        log.info("메시지 전송 완료. receiverId={}, roomUuid={}", receiverId, roomUuid);
    }
}