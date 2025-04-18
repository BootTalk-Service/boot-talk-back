package com.icandoit.boottalk.stomp_chat.controller;

import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatMessageRequestDto;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatTypingRequestDto;
import com.icandoit.boottalk.stomp_chat.service.ChatWebsocketService;
import java.security.Principal;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
    public void send(@Payload ChatMessageRequestDto requestDto, Authentication authentication) {
        if (authentication == null) {
            log.error("인증 정보가 없습니다.");
            throw new AccessDeniedException("인증되지 않은 사용자");
        }

        // Principal 대신 Authentication을 사용하여 더 많은 정보에 접근
        String username = authentication.getName();
        log.info("메시지 발신자: {}, 권한: {}", username, authentication.getAuthorities());

        // CustomOAuth2User 타입인 경우 추가 정보 접근 가능
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            log.info("사용자 ID: {}", user.getServiceUserId());
        }

        chatWebsocketService.saveAndSendMessage(requestDto.senderId(), requestDto);
    }

    @MessageMapping("/chat.enter/{roomUuid}")
    public void enter(@DestinationVariable String roomUuid, Principal principal) {
        if (principal instanceof Authentication authentication) {
            if (authentication.getPrincipal() instanceof CustomOAuth2User user) {
                Long userId = user.getServiceUserId();
                log.info("채팅 입장: userId = {}", userId);
                chatWebsocketService.handleUserEnter(userId, roomUuid);
            } else {
                log.error("CustomOAuth2User 타입이 아닙니다. principal: {}", authentication.getPrincipal());
            }
        } else {
            log.error("Authentication 객체가 아닙니다. principal: {}", principal);
        }
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatTypingRequestDto requestDto, Principal principal) {
        Long senderId = Long.parseLong(principal.getName());
//        Long senderId = 8050L;
        chatWebsocketService.sendTypingStatus(senderId, requestDto);
    }
}