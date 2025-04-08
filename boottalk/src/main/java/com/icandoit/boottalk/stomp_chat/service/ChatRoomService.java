package com.icandoit.boottalk.stomp_chat.service;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.stomp_chat.entity.ChatRoom;
import com.icandoit.boottalk.stomp_chat.repository.ChatRoomRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final CoffeeChatApplicationRepository coffeeChatApplicationRepository;

    public UUID createChatRoom(Long applicationId) {
        CoffeeChatApplication application = coffeeChatApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_NOT_FOUND));

        // 이미 생성된 방이 있는지 확인
        chatRoomRepository.findByCoffeeChatApplication(application)
            .ifPresent(room -> {
                log.warn("이미 채팅방이 존재합니다: {}", room.getRoomUuid());
                throw new CustomException(ErrorCode.COFFEE_CHAT_ALREADY_EXISTS);
            });

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.of(application);
        chatRoomRepository.save(chatRoom);
        return UUID.fromString(chatRoom.getRoomUuid());
    }
}
