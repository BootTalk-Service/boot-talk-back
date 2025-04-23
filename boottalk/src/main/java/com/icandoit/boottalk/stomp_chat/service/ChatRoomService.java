package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomCreateResponse;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.scheduler.ChatQuartzSchedulerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatQuartzSchedulerService chatQuartzSchedulerService;

  
    // 채팅방 생성(커피챗 승인 시 생성)
    @Transactional
    public ChatRoomCreateResponse createChatRoom(CoffeeChatApplication application) {

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.of(application));
        chatQuartzSchedulerService.scheduleStartAndEndJobs(
            chatRoom.getRoomUuid(),
            application.getCoffeeChatStartTime(),
            application.getCoffeeChatEndTime()
        );
        return ChatRoomCreateResponse.from(chatRoom.getRoomUuid());
    }

    // 채팅방 목록 조회
    public List<ChatRoomResponseDto> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(userId);
        return chatRooms.stream()
            .map(ChatRoomResponseDto::from)
            .toList();
    }

    // 채팅 메시지 조회 (입장)
    public List<ChatMessageResponseDto> getMessages(Long userId, String roomUuid) {

        validateUserParticipantInRoom(userId, roomUuid);

        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomUuid(roomUuid);

        return chatMessages.stream()
            .map(ChatMessageResponseDto::from)
            .toList();
    }

    // 방 생성(isActive = true)
    @Transactional
    public void activateChatRoom(String roomUuid) {
        ChatRoom room = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        room.getRoomStatus().setActive(true);
    }

    @Transactional
    public void endCoffeeChat(String roomUuid) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoom.getRoomStatus().setActive(false);
        chatRoom.getCoffeeChatApplication().setStatus(StatusType.COMPLETED);
        log.info("커피챗 종료- 상태 변경 완료, roomUuid {}", roomUuid);
    }

    private void validateUserParticipantInRoom(Long userId, String roomUuid) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.isParticipant(userId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_FORBIDDEN);
        }
    }
}