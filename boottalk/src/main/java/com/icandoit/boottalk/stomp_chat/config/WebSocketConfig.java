package com.icandoit.boottalk.stomp_chat.config;

import com.icandoit.boottalk.social_login.jwt.JwtProvider;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connection")
            .setAllowedOriginPatterns("*")
            .addInterceptors(new HandshakeInterceptor() {
                @Override
                public boolean beforeHandshake(ServerHttpRequest request,
                    ServerHttpResponse response,
                    WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                    // URL 파라미터에서 토큰 추출
                    String token = null;
                    if (request instanceof ServletServerHttpRequest) {
                        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                        token = servletRequest.getServletRequest().getParameter("token");
                        log.info("WebSocket 연결 시도: 토큰 = {}", token);

                        if (token != null && jwtProvider.validateToken(token)) {
                            // 토큰에서 사용자 ID 추출하여 WebSocket 세션에 저장
                            String userId = jwtProvider.getUserIdFromToken(token);
                            attributes.put("userId", userId);

                            // *** 중요: 인증 객체 생성 및 저장 ***
                            Authentication auth = jwtProvider.getAuthentication(token);
                            SecurityContext securityContext = new SecurityContextImpl(auth);
                            attributes.put("SPRING_SECURITY_CONTEXT", securityContext);

                            log.info("WebSocket 연결 성공: 사용자 ID = {}, Auth = {}", userId, auth);
                            return true;
                        }
                    }
                    log.error("WebSocket 연결 실패: 유효하지 않은 토큰");
                    return false;
                }

                @Override
                public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                    WebSocketHandler wsHandler, Exception exception) {
                    // 핸드셰이크 후 처리 (필요한 경우)
                }
            });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/queue");
    }

    // WebSocket 연결 인증을 위한 채널 인터셉터 추가
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
                    StompHeaderAccessor.class);

                if (accessor != null && accessor.getSessionAttributes() != null) {
                    // 모든 메시지 명령에 대해 처리 (CONNECT, SEND, SUBSCRIBE 등)
                    SecurityContext securityContext = (SecurityContext) accessor.getSessionAttributes()
                        .get("SPRING_SECURITY_CONTEXT");

                    if (securityContext != null && securityContext.getAuthentication() != null) {
                        // 현재 스레드에 SecurityContext 설정
                        SecurityContextHolder.setContext(securityContext);

                        // *** 중요: Principal 객체를 메시지 헤더에 설정 ***
                        accessor.setUser(securityContext.getAuthentication());

                        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                            log.info("STOMP 연결 설정: 인증 정보 설정 완료. userId = {}, auth = {}",
                                accessor.getSessionAttributes().get("userId"),
                                securityContext.getAuthentication());
                        } else if (StompCommand.SEND.equals(accessor.getCommand()) ||
                            StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                            log.info("메시지 전송/구독: 인증 정보 확인됨. user = {}",
                                securityContext.getAuthentication().getName());
                        }
                    } else {
                        log.warn("SecurityContext가 없거나 인증 정보가 없습니다.");
                    }
                }

                return message;
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel,
                boolean sent, Exception ex) {
                // 메시지 처리 완료 후 SecurityContext 정리
                SecurityContextHolder.clearContext();
            }
        });
    }
}