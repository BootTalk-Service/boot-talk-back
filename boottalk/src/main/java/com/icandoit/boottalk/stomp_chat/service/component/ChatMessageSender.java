package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.MessageResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ChatMessageSender {

    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations template;

    @Transactional
    public void enterSystemMessage(Long userId, String roomUuid) {

        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 상대방 ID 찾기
        Long receiverId = chatRoom.getMentor().getUserId().equals(userId)
            ? chatRoom.getMentee().getUserId()
            : chatRoom.getMentor().getUserId();

        // 입장 메시지 생성
        ChatMessage enterMessage = ChatMessage.of(
            roomUuid,
            userId,
            receiverId,
            "입장하였습니다.",
            MessageType.SYSTEM
        );

        // todo: 입장 메시지도 저장을 해야할까?
        messageRepository.save(enterMessage);

        // WebSocket 전송
        String destination = "/queue/chat/" + roomUuid + "/" + userId;
        template.convertAndSend(destination, MessageResponseDto.from(enterMessage));
    }

}