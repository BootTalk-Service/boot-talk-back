package com.icandoit.boottalk.stomp_chat.config;

import com.icandoit.boottalk.social_login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtProvider jwtProvider;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

		if (request instanceof ServletServerHttpRequest servletRequest) {
			String token = servletRequest.getServletRequest().getParameter("token");
			log.info("[Handshake] 받은 토큰: {}", token);

			if (token != null && jwtProvider.validateToken(token)) {
				Authentication auth = jwtProvider.getAuthentication(token);
				attributes.put("auth", auth);
				log.info("[Handshake] WebSocket 세션에 인증 정보 저장 완료: {}", auth.getName());
				return true;
			}
		}

		log.error("[Handshake] 토큰이 유효하지 않습니다. WebSocket 연결 거부");
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Exception exception) {
		// 생략
	}
}
