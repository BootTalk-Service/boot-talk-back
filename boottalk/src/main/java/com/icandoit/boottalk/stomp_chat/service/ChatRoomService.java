package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomCreateResponse;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomResponseDto;
import com.icandoit.boottalk.stomp_chat.dto.MessageRequestDto;
import com.icandoit.boottalk.stomp_chat.dto.MessageResponseDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.Message;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import com.icandoit.boottalk.stomp_chat.repository.MessageRepository;
import com.icandoit.boottalk.stomp_chat.util.SystemMessageUtil;
import java.time.LocalDateTime;
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
    private final CoffeeChatApplicationRepository coffeeChatApplicationRepository;
    private final ChatWebsocketService chatWebsocketService;
    private final MessageFactoryService messageFactory;
    private final MessageRepository messageRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoomCreateResponse createChatRoom(Long applicationId) {

        CoffeeChatApplication application = coffeeChatApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_NOT_FOUND));

        // 이미 생성된 방이 있는지 확인
        chatRoomRepository.findByCoffeeChatApplication(application)
            .ifPresent(room -> {
                log.warn("이미 채팅방이 존재합니다: {}", room.getRoomUuid());
                throw new CustomException(ErrorCode.COFFEE_CHAT_ALREADY_EXISTS);
            });

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.of(application));

        return ChatRoomCreateResponse.from(chatRoom.getRoomUuid());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findActiveChatRoomsByUserId(userId);
        return chatRooms.stream()
            .map(ChatRoomResponseDto::from)
            .toList();
    }

    // 채팅 메시지 기록 조회
    public List<MessageResponseDto> getMessages(Long userId, String roomUuid) {
        ChatRoom chatRoom = getChatRoom(roomUuid);

        // 해당 채팅방에 참여한 유저인지 체크
        validateChatRoomEntry(chatRoom, userId);

        // 만료일 체크
        if (chatRoom.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.MESSAGE_EXPIRED);
        }

        List<Message> messages = messageRepository.findByRoomUuid(roomUuid);

        return messages.stream()
            .map(MessageResponseDto::from)
            .toList();
    }


    // 채팅방 입장
    @Transactional
    public void enterChatRoom(String roomUuid, Long userId) {

        ChatRoom chatRoom = getValidChatRoom(roomUuid);
        validateChatRoomEntry(chatRoom, userId);

        if (isMentor(chatRoom, userId)) {
            handleMentorEntry(chatRoom);
        }

        if (isMentee(chatRoom, userId)) {
            handleMenteeEntry(chatRoom);
        }

        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 퇴장
    public void leaveChatRoom(Long userId, String roomUuid) {
        ChatRoom chatRoom = getChatRoom(roomUuid);
        validateChatRoomEntry(chatRoom, userId);

        if (isMentor(chatRoom, userId)) {
            chatRoom.setMentorEntered(false);
        }

        if (isMentee(chatRoom, userId)) {
            chatRoom.setMenteeEntered(false);
        }
        chatRoomRepository.save(chatRoom);
    }

    private ChatRoom getValidChatRoom(String roomUuid) {
        ChatRoom chatRoom = getChatRoom(roomUuid);

        if (!chatRoom.isActive()) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_ACTIVE);
        }

        if (LocalDateTime.now().isAfter(chatRoom.getExpiresAt())) {
            chatRoom.setActive(false);
            chatRoomRepository.save(chatRoom);
        }

        return chatRoom;
    }

    private ChatRoom getChatRoom(String roomUuid) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return chatRoom;
    }

    // 참여자인지 확인
    private void validateChatRoomEntry(ChatRoom chatRoom, Long userId) {
        boolean isMentor = isMentor(chatRoom, userId);
        boolean isMentee = isMentee(chatRoom, userId);

        if (!isMentor && !isMentee) {
            throw new CustomException(ErrorCode.CHAT_ROOM_FORBIDDEN);
        }
    }

    private boolean isMentor(ChatRoom chatRoom, Long userId) {
        return chatRoom.getMentor().getUserId().equals(userId);
    }

    private boolean isMentee(ChatRoom chatRoom, Long userId) {
        return chatRoom.getMentee().getUserId().equals(userId);
    }

    private void handleMentorEntry(ChatRoom chatRoom) {
        if (!chatRoom.isMentorEntered()) {
            chatRoom.setMentorEntered(true);

            MessageRequestDto systemMessage = messageFactory.createSystemMessage(
                chatRoom.getRoomUuid(),
                chatRoom.getMentee().getUserId(),
                SystemMessageUtil.MentorEnterMessage(chatRoom.getMentor().getUserName())
            );

            chatWebsocketService.saveAndSendMessage(systemMessage);
        }
    }

    private void handleMenteeEntry(ChatRoom chatRoom) {
        if (!chatRoom.isMenteeEntered()) {
            chatRoom.setMenteeEntered(true);

            MessageRequestDto systemMessage = messageFactory.createSystemMessage(
                chatRoom.getRoomUuid(),
                chatRoom.getMentor().getUserId(),
                SystemMessageUtil.MenteeEnterMessage(chatRoom.getMentee().getUserName())
            );

            chatWebsocketService.saveAndSendMessage(systemMessage);
        }
    }
}
