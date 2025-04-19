package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.ChatMessageResponseDto;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomCreateResponse;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatMessage;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoomStatus;
import com.icandoit.boottalk.stomp_chat.repository.ChatMessageRepository;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomStatusRepository;
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
    private final ChatRoomStatusRepository chatRoomStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatQuartzSchedulerService chatQuartzSchedulerService;

    // 채팅방 생성
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
        ChatRoomStatus chatRoomStatus = getChatRoomStatus(roomUuid);
        ChatRoom chatRoom = chatRoomStatus.getChatRoom();

        validateChatRoomEntry(chatRoom, userId);

        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomUuid(roomUuid);

        return chatMessages.stream()
            .map(ChatMessageResponseDto::from)
            .toList();
    }


    // ChatRoomStatus 조회
    private ChatRoomStatus getChatRoomStatus(String roomUuid) {
        return chatRoomStatusRepository.findByChatRoom_RoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    // 채팅방 입장 권한 확인
    private void validateChatRoomEntry(ChatRoom chatRoom, Long userId) {
        if (!isMentor(chatRoom, userId) && !isMentee(chatRoom, userId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_FORBIDDEN);
        }
    }

    // 멘토인지 확인
    private boolean isMentor(ChatRoom chatRoom, Long userId) {
        return chatRoom.getMentor().getUserId().equals(userId);
    }

    // 멘티인지 확인
    private boolean isMentee(ChatRoom chatRoom, Long userId) {
        return chatRoom.getMentee().getUserId().equals(userId);
    }
}