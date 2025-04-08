package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.dto.ChatRoomCreateResponse;
import com.icandoit.boottalk.stomp_chat.dto.MessageRequestDto;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.entity.enums.MessageType;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
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
    private final MessageService messageService;

    private static final Long SYSTEM_SENDER_ID = 0L; // 시스템이 메시지를 보낼 때


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

        // 시스템 메시지 생성
        MessageRequestDto systemMessage = MessageRequestDto.from(
            chatRoom.getRoomUuid(),
            SYSTEM_SENDER_ID,
            chatRoom.getMentee().getUserId(),   // 멘티에게 보내는 시스템 메시지
            SystemMessageUtil.createSystemMessage(
                chatRoom.getCreatedAt()),
            MessageType.SYSTEM
        );

        // 시스템 메시지 저장 및 전송
        messageService.saveAndSendMessage(systemMessage);
        return ChatRoomCreateResponse.from(chatRoom.getRoomUuid());
    }

    // 사용자가 속한 채팅방 조회
    @Transactional(readOnly = true)
    public List<ChatRoom> getUserChatRooms(Long userId) {
        return chatRoomRepository.findActiveChatRoomsByUserId(userId);
    }

    // 채팅방 입장
    @Transactional
    public void  enterChatRoom(String roomUuid, Long userId) {

        // 유효한 채팅방 상태 검증 및 조회
        ChatRoom chatRoom = getValidChatRoom(roomUuid);

        // 입장 권한 확인
        validateChatRoomEntry(chatRoom, userId);

        // 멘토인 경우
        if (isMentor(chatRoom, userId)) {
            handleMentorEntry(chatRoom);
        }

        // 멘티인 경우
        if (isMentee(chatRoom, userId)) {
            handleMenteeEntry(chatRoom);
        }

        chatRoomRepository.save(chatRoom);
    }

    private ChatRoom getValidChatRoom(String roomUuid) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
            .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.isActive()) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_ACTIVE);
        }

        if (LocalDateTime.now().isAfter(chatRoom.getExpiresAt())) {
            chatRoom.setActive(false);
            chatRoomRepository.save(chatRoom);
            throw new CustomException(ErrorCode.CHAT_ROOM_EXPIRED);
        }

        return chatRoom;
    }

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

            MessageRequestDto systemMessage = MessageRequestDto.from(
                chatRoom.getRoomUuid(),
                SYSTEM_SENDER_ID,
                chatRoom.getMentee().getUserId(),
                SystemMessageUtil.MentorEnterMessage(chatRoom.getMentor().getUserName()),
                MessageType.SYSTEM
            );

            messageService.saveAndSendMessage(systemMessage);
        }
    }

    private void handleMenteeEntry(ChatRoom chatRoom) {
        if (!chatRoom.isMenteeEntered()) {
            chatRoom.setMenteeEntered(true);

            MessageRequestDto systemMessage = MessageRequestDto.from(
                chatRoom.getRoomUuid(),
                SYSTEM_SENDER_ID,
                chatRoom.getMentor().getUserId(),
                SystemMessageUtil.MenteeEnterMessage(chatRoom.getMentee().getUserName()),
                MessageType.SYSTEM
            );

            messageService.saveAndSendMessage(systemMessage);
        }
    }
}
