package com.icandoit.boottalk.stomp_chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 클라이언트가 연결할 WebSocket 엔드포인트 정의
		// 클라이언트는 이 엔드포인트로 연결하여 WebSocket 핸드셰이크를 해야 함
		registry.addEndpoint("/connection")
			.setAllowedOriginPatterns("*"); // 모든 출처에서의 CORS 요청 허용
			// .withSockJS(); // WebSocket 을 지원하지 않는 브라우저에서 SockJS 로 대체 연결을 사용하도록 할 수 있음
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		// 클라이언트가 메시지를 보낼 때 사용하는 API의 prefix를 설정
		// "/app" 으로 시작하는 STOMP 메시지는 이 설정에 따라 라우팅 됨
		// 클라이언트에서 보낸 메시지의 주소가 /app 으로 시작하는 경우, 이 메시지는 서버에서 처리됨
		// 즉, 애플리케이션 내부에서 메시지 처리를 위한 경로이다.
		registry.setApplicationDestinationPrefixes("/app");

		// 메시지 브로커를 설정하여 클라이언트가 구독할 수 있는 주제를 지정
		// 클라이언트는 "/queue" 로 시작하는 주제를 구독하고, 메시지가 이 주제에 전달되면
		// 메시지 브로커가 이를 자동으로 구독한 클라이언트에게 전달함
		// 즉, "/queue" 로 시작하는 주제는 브로커가 관리하고 여러 클라이언트에게 전달됨
		registry.enableSimpleBroker("/queue");
	}
}
