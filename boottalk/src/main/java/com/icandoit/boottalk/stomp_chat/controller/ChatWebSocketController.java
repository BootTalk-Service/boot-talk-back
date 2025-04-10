package com.icandoit.boottalk.stomp_chat.controller;

import com.icandoit.boottalk.stomp_chat.dto.ChatRoomEnterDto;
import com.icandoit.boottalk.stomp_chat.dto.MessageRequestDto;
import com.icandoit.boottalk.stomp_chat.service.ChatWebsocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatWebsocketService chatWebsocketService;


    @MessageMapping("/chat/enter")
    public void enter(@Payload ChatRoomEnterDto requstDto) {
        // 이전 메시지 조회 및 전송 (엔터 유저 기준 큐로)
        chatWebsocketService.sendPreviousMessages(requstDto);
    }

    /*
      1:1 채팅 메시지 처리 (메시지를 처리하고 특정 사용자에게 전송)
        - 클라이언트가 /app/chat/send/ 로 메시지를 보내면 호출됨
        - /app: 클라이언트가 WebSocket 을 통해 서버에 메시지를 보낼 때 사용되는 기본 경로로, 서버에서 @MessageMapping 을 처리하는 메소드로 라우팅됨
        - /queue: 서버가 클라이언트에게 메시지를 보낼 때 사용하는 경로로, 주로 특정 클라이언트나 사용자에게 메시지를 전송하는데 사용됨
     */
    @MessageMapping("/chat/send")
    public void send(@Payload MessageRequestDto requestDtodto) {
        chatWebsocketService.saveAndSendMessage(requestDtodto);
    }
}