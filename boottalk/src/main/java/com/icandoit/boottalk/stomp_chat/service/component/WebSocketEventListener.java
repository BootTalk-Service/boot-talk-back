package com.icandoit.boottalk.stomp_chat.service.component;

import com.icandoit.boottalk.stomp_chat.service.ChatWebsocketService;
import java.util.Map;
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

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes == null || sessionAttributes.get("userId") == null) {
            log.warn("세션에 userId가 없음 - 연결 해제 무시");
            return;
        }

        // 세션에서 가져온 userId의 타입 확인 및 변환
        Object userIdObj = sessionAttributes.get("userId");
        Long userId;

        if (userIdObj instanceof Long) {
            // 이미 Long 타입인 경우
            userId = (Long) userIdObj;
            log.info("if문 테스트 : userId 는 Long 타입입니다. ");
        } else if (userIdObj instanceof String) {
            // todo: 테스트 후 제거 예정
            try {
                userId = Long.parseLong((String) userIdObj);
            } catch (NumberFormatException e) {
                log.error("userId를 Long으로 변환할 수 없습니다: {}", userIdObj);
                return;
            }
        } else {
            // 다른 타입인 경우
            log.error("지원되지 않는 userId 타입: {}", userIdObj.getClass().getName());
            return;
        }

        log.info("사용자 연결 종료: {}", userId);
        chatWebsocketService.handleUserLeave(userId);
    }
}