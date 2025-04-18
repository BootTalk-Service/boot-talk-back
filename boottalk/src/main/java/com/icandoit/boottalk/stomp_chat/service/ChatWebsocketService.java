package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatMessageRequestDto;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatTypingRequestDto;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatTypingResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatUserRepository;
import com.icandoit.boottalk.stomp_chat.service.component.ChatMessageSender;
import com.icandoit.boottalk.stomp_chat.service.component.ChatRoomStatusUpdater;
import com.icandoit.boottalk.stomp_chat.service.component.MessageLoader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatWebsocketService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatMessageRepository redisChatRepository;
    private final RedisChatRoomRepository redisRoomRepository;
    private final RedisChatUserRepository redisChatUserRepository;

    private final ChatRoomStatusUpdater statusUpdater;
    private final ChatMessageSender messageSender;
    private final MessageLoader messageLoader;
    private final SimpMessagingTemplate template;


    @Transactional
    public void handleUserEnter(Long userId, String roomUuid) {
        LocalDateTime enterTime = LocalDateTime.now();

        // 입장한 유저상태 저장
        redisChatUserRepository.saveUserEnterStatus(roomUuid, userId);
        // 메시지 조회 및 전송
        messageLoader.loadAndSendMessages(userId, roomUuid);

        // 채팅방 상태 갱신
        statusUpdater.updateChatRoomStatusOnEnter(userId, roomUuid);

        // 읽음 처리
        markUnreadMessagesAsRead(roomUuid, userId, enterTime);

        messageSender.sendEnterMessage(userId, roomUuid);
    }

    // Listener 호출(퇴장 시 호출되는 매서드)
    @Transactional
    public void handleUserLeave(Long userId) {
        statusUpdater.updateChatRoomStatusOnLeave(userId);
    }

    @Transactional
    public void saveAndSendMessage(Long senderId, ChatMessageRequestDto requestDto) {
        String roomUuid = requestDto.roomUuid();

        // 커피챗 예약 시간 내에만 채팅 가능하도록 체크
        checkChatRoomWithinAllowedTime(roomUuid);
        boolean isReceiverInRoom = redisChatUserRepository.hasUserEntered(roomUuid,
            requestDto.receiverId());
        log.info("senderId: {}, receiverId: {}", senderId, requestDto.receiverId());

        // 메시지를 DTO로 변환
        ChatMessageResponseDto messageToCache = new ChatMessageResponseDto(
            roomUuid,
            senderId,
            requestDto.receiverId(),
            requestDto.content(),
            requestDto.type(),
            LocalDateTime.now(),
            isReceiverInRoom
        );

        // Redis에 저장
        saveMessageWithFallback(messageToCache);
        log.info("메시지 Redis에 캐시됨: {}", messageToCache);
        // 메시지 WebSocket으로 전송
        messageSender.sendMessage(requestDto.receiverId(), messageToCache);
    }

    public void sendTypingStatus(Long senderId, ChatTypingRequestDto requestDto) {

        ChatTypingResponseDto response = new ChatTypingResponseDto(senderId, requestDto.typing());

        String destination = "/queue/chat/" + requestDto.roomUuid() + "/" + requestDto.receiverId();
        template.convertAndSend(destination, response);
    }

    public void markUnreadMessagesAsRead(String roomUuid, Long userId, LocalDateTime enterTime) {
        List<ChatMessageResponseDto> cachedMessages = redisChatRepository.getMessages(roomUuid);

        boolean updated = false;

        for (ChatMessageResponseDto message : cachedMessages) {
            if (Objects.equals(message.getReceiverId(), userId)
                && !message.isRead()
                && message.getSentAt().isBefore(enterTime)) {
                message.markAsRead();
                updated = true;
            }
        }

        if (updated) {
            redisChatRepository.saveAll(roomUuid, cachedMessages, Duration.ofMinutes(30));
        }

        log.info("입장 시 읽음 처리 완료 for userId={}, roomUuid={}", userId, roomUuid);
    }


    private void checkChatRoomWithinAllowedTime(String roomUuid) {
        // Redis에서 채팅방 정보 조회
        ChatRoomResponseDto chatRoomDto = redisRoomRepository.findChatRoomByRoomUuidFromCache(
            roomUuid);

        // 없다면 redis 저장
        if (chatRoomDto == null) {
            ChatRoom chatRoomEntity = chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

            chatRoomDto = ChatRoomResponseDto.from(chatRoomEntity);
            redisRoomRepository.saveChatRoomToCache(chatRoomDto);
        }

        if (LocalDateTime.now().isBefore(chatRoomDto.reservationAt())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_STARTED);
        }

        if (LocalDateTime.now().isAfter(chatRoomDto.endAt())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ENDED);
        }
    }

    public void saveMessageWithFallback(ChatMessageResponseDto message) {
        boolean isSavedToRedis = false;
        try {
            redisChatRepository.save(message.getRoomUuid(), message, Duration.ofMinutes(30));
            isSavedToRedis = true;
        } catch (RedisConnectionFailureException ex) {
            log.error("Redis 장애 발생: 메시지를 Redis에 저장하지 못했습니다. DB에 저장합니다.");
        }

        if (!isSavedToRedis) {
            ChatMessage chatMessage = message.toEntity();
            chatMessageRepository.save(chatMessage);
            log.error("Redis 장애 발생, 메시지 DB로 저장됨: {}", message);
        }
    }
}
