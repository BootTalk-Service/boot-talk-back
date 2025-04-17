package com.icandoit.boottalk.stomp_chat.controller;

import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatMessageRequestDto;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatTypingRequestDto;
import com.icandoit.boottalk.stomp_chat.service.ChatWebsocketService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatWebsocketService chatWebsocketService;

    /*
      1:1 채팅 메시지 처리 (메시지를 처리하고 특정 사용자에게 전송)
        - /app: 클라이언트가 WebSocket 을 통해 서버에 메시지를 보낼 때 사용되는 기본 경로로, 서버에서 @MessageMapping 을 처리하는 메소드로 라우팅됨
        - /queue: 서버가 클라이언트에게 메시지를 보낼 때 사용하는 경로로, 주로 특정 클라이언트나 사용자에게 메시지를 전송하는데 사용됨
     */

    @MessageMapping("/chat.message")
    public void send(@Payload ChatMessageRequestDto requstDto, Principal principal) {
//        Long senderId = Long.parseLong(principal.getName());
        Long senderId = 8050L;
        log.info("parsing principal user = {}", senderId);
        chatWebsocketService.saveAndSendMessage(senderId, requstDto);
    }

    @MessageMapping("/chat.enter/{roomUuid}")
    public void enter(@DestinationVariable String roomUuid, Principal principal) {
//        Long userId = Long.parseLong(principal.getName());
        Long userId = 8050L;
        log.info("parsing principal user = {}", userId);
        // 이전 메시지 조회 및 전송
        chatWebsocketService.handleUserEnter(userId, roomUuid);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatTypingRequestDto requestDto, Principal principal) {
//        Long senderId = Long.parseLong(principal.getName());
        Long senderId = 8050L;
        chatWebsocketService.sendTypingStatus(senderId, requestDto);
    }
}