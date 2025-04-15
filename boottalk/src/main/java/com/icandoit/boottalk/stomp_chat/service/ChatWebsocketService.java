package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.MessageResponseDto;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatMessageRequestDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.RedisChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.service.component.ChatMessageSender;
import com.icandoit.boottalk.stomp_chat.service.component.ChatRoomStatusUpdater;
import com.icandoit.boottalk.stomp_chat.service.component.MessageLoader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final ChatRoomStatusUpdater statusUpdater;
    private final ChatMessageSender messageSender;
    private final MessageLoader messageLoader;
    private final SimpMessagingTemplate template;

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분마다 실행
    public void flushRedisToDatabase() {
        List<ChatMessage> messagesToFlush = redisChatRepository.getMessagesForBatch();

        if (!messagesToFlush.isEmpty()) {
            chatMessageRepository.saveAll(messagesToFlush);
            log.info("Redis에서 DB로 메시지 {}개 저장됨", messagesToFlush.size());
            redisChatRepository.deleteMessages(messagesToFlush);
        }
    }

    @Transactional
    public void handleUserEnter(Long userId, String roomUuid) {
        // 1. 메시지 조회 및 전송
        messageLoader.loadAndSendMessages(userId, roomUuid);

        // 2. 채팅방 상태 갱신
        statusUpdater.updateChatRoomStatusOnEnter(userId, roomUuid);

        // 3. 유저 입장 메시지가 필요한 경우 전송
        messageSender.enterSystemMessage(userId, roomUuid);
    }

    @Transactional
    public void saveAndSendMessage(Long senderId, ChatMessageRequestDto requestDto) {
        String roomUuid = requestDto.roomUuid();

        // 커피챗 예약 시간 내에만 채팅 가능하도록 체크
        checkChatRoomWithinAllowedTime(roomUuid);

        // 메시지를 Redis에 먼저 저장 (TTL 30분)
        ChatMessage messageToCache = ChatMessage.of(
            roomUuid,
            senderId,
            requestDto.receiverId(),
            requestDto.content(),
            requestDto.type()
        );

        // Redis에 저장
        saveMessageWithFallback(messageToCache);

        // 메시지 WebSocket으로 전송
        String destination = "/queue/chat/" + roomUuid + "/" + requestDto.receiverId();
        template.convertAndSend(destination, MessageResponseDto.from(messageToCache));

        log.info("메시지 Redis에 캐시됨. roomUuid={}, senderId={}, receiverId={}, message={}, type={}",
            roomUuid, senderId, requestDto.receiverId(), requestDto.content(), requestDto.type());
    }

    private void checkChatRoomWithinAllowedTime(String roomUuid) {
        // Redis에서 채팅방 정보 조회
        ChatRoom chatRoom = redisRoomRepository.get(roomUuid)
            .orElseGet(() -> {
                // Redis에 캐시된 정보가 없으면 DB에서 조회 후 캐시 저장
                ChatRoom dbChatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
                    .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
                redisRoomRepository.save(dbChatRoom); // DB에서 조회한 채팅방을 Redis에 저장
                return dbChatRoom;
            });

        // 채팅방 예약 시간 확인
        if (LocalDateTime.now().isBefore(chatRoom.getReservationAt())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_STARTED);
        }

        if (LocalDateTime.now().isAfter(chatRoom.getEndAt())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ENDED);
        }
    }

    public void saveMessageWithFallback(ChatMessage message) {
        try {
            redisChatRepository.save(message.getRoomUuid(), message, Duration.ofMinutes(30));
        } catch (RedisConnectionFailureException ex) {
            chatMessageRepository.save(message); // Redis 장애 시 DB에만 저장
            log.error("Redis 장애 발생, 메시지 DB로 저장됨: {}", message);
        }
    }
}
