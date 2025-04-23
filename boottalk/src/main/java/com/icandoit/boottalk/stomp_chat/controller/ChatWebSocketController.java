package com.icandoit.boottalk.stomp_chat.controller;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatMessageRequestDto;
import com.icandoit.boottalk.stomp_chat.dto.stompDto.ChatTypingRequestDto;
import com.icandoit.boottalk.stomp_chat.service.ChatWebsocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatWebsocketService chatWebsocketService;

    @MessageMapping("/chat.message")
    public void send(@Payload ChatMessageRequestDto requestDto, Authentication authentication) {
        if (authentication == null) {
            log.error("인증 정보가 없습니다.");
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        Long userId = extractUserId(authentication);
        log.info("메시지 발신자: {}, userId: {}", authentication.getName(), userId);

        chatWebsocketService.saveAndSendMessage(requestDto.senderId(), requestDto);
    }

    @MessageMapping("/chat.enter/{roomUuid}")
    public void enter(@DestinationVariable String roomUuid, Authentication authentication) {
        if (authentication == null) {
            log.error("인증 정보가 없습니다.");
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = user.getServiceUserId();
        String userName = user.getName();
        log.info("채팅 입장: userId = {}", userId);
        chatWebsocketService.handleUserEnter(userId, userName, roomUuid);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatTypingRequestDto requestDto, Authentication authentication) {
        if (authentication == null) {
            log.error("인증 정보가 없습니다.");
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        chatWebsocketService.sendTypingStatus(requestDto);
    }

    // 사용자 ID를 추출하는 공통 메서드
    private Long extractUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomOAuth2User user) {
            return user.getServiceUserId();
        }
        log.warn("CustomOAuth2User 타입이 아닙니다. 기본 인증 방식 사용");

        throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_TYPE);
    }
}