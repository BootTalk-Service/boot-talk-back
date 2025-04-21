package com.icandoit.boottalk.stomp_chat.service.component;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.stomp_chat.service.ChatWebsocketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes == null || sessionAttributes.get("auth") == null) {
            log.warn("세션에 auth 없음 - 연결 해제 무시");
            return;
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)sessionAttributes.get("auth");
        CustomOAuth2User user = (CustomOAuth2User)auth.getPrincipal();
        Long userId = user.getServiceUserId();

        log.info("사용자 연결 종료: {}", userId);
        chatWebsocketService.handleUserLeave(userId);
    }
}