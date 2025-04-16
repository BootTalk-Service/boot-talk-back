package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.stomp_chat.service.ChatWebsocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final ChatWebsocketService chatWebsocketService;

    //유저가 WebSocket 연결을 끊었을 때 호출되는 이벤트 핸들러
    //(예: 브라우저 종료, 새로고침 등)
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        // WebSocket 세션에서 STOMP 헤더를 가져옴
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Long userId = (Long) accessor.getSessionAttributes().get("userId");

        if (userId == null) {
            log.warn("세션에 userId가 없음 - 연결 해제 무시");
            return;
        }
        chatWebsocketService.handleUserLeave(userId);
    }
}