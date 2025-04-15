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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatMessageSender {

    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations template;
    private final RedisTemplate<String, Object> redisTemplate;

    public static final String REDIS_CHAT_ENTERED_PREFIX = "chat:entered:";

    public void enterSystemMessage(Long userId, String roomUuid) {
        // Redis에서 사용자가 이미 입장했는지 체크
        boolean hasEntered =
            redisTemplate.opsForValue().get(REDIS_CHAT_ENTERED_PREFIX + roomUuid + ":" + userId) != null;

        if (hasEntered) {
            // 이미 입장한 경우 시스템 메시지 전송 안 함
            return;
        }

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

        // 입장 메시지 저장
        messageRepository.save(enterMessage);

        // WebSocket 전송
        String destination = "/queue/chat/" + roomUuid + "/" + userId;
        template.convertAndSend(destination, MessageResponseDto.from(enterMessage));

        // Redis에 입장 정보 저장 (입장한 사용자 ID 기록)
        redisTemplate.opsForValue().set(REDIS_CHAT_ENTERED_PREFIX + roomUuid + ":" + userId, true);
    }
}