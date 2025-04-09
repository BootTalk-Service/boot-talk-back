package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomEnterDto;
import com.icandoit.boottalk.stomp_chat.dto.MessageRequestDto;
import com.icandoit.boottalk.stomp_chat.dto.MessageResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.Message;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.MessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatWebsocketService {

    private final SimpMessagingTemplate template;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional(readOnly = true)
    public void sendPreviousMessages(ChatRoomEnterDto requestDto) {
        List<Message> messages = messageRepository.findByRoomUuid(requestDto.chatRoomUuid());

        List<MessageResponseDto> responseDtos = messages.stream()
            .map(MessageResponseDto::from)
            .toList();

        log.info("유저가 채팅방에 입장했습니다. userId={}, roomUuid={}", requestDto.enterUserId(), requestDto.chatRoomUuid());

        String destination =
            "/queue/chat/" + requestDto.chatRoomUuid() + "/" + requestDto.enterUserId();
        template.convertAndSend(destination, responseDtos);
    }

    @Transactional
    public void saveAndSendMessage(MessageRequestDto requestDto) {

        // 커피챗 예약 시간 내에만 채팅 가능하도록 수정
        checkChatRoomWithinAllowedTime(requestDto);

        Message saved = messageRepository.save(Message.of(
            requestDto.roomUuid(),
            requestDto.senderId(),
            requestDto.senderName(),
            requestDto.receiverId(),
            requestDto.message(),
            requestDto.type()
        ));
        log.info("메시지 전송됨. roomUuid={}, senderId={}, receiverId={}, type={}, message={}",
            requestDto.roomUuid(),
            requestDto.senderId(),
            requestDto.receiverId(),
            requestDto.type(),
            requestDto.message()
        );
        String destination = "/queue/chat/" + requestDto.roomUuid() + "/" + requestDto.receiverId();
        template.convertAndSend(destination, MessageResponseDto.from(saved));
    }

    private void checkChatRoomWithinAllowedTime(MessageRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(requestDto.roomUuid())
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (LocalDateTime.now().isBefore(chatRoom.getReservationAt())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_STARTED);
        }

        if (LocalDateTime.now().isAfter(chatRoom.getEndAt())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ENDED);
        }
    }
}
