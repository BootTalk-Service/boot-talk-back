package com.icandoit.boottalk.social_login.config.exception;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icandoit.boottalk.libs.dto.ExceptionResponseDto;
import com.icandoit.boottalk.libs.exception.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		log.error("사용자 인증 실패 : {}, URI: {}", authException.getMessage(), request.getRequestURI());

		String uri = request.getRequestURI();

		// OAuth2 인증 관련 경로는 처리하지 않고 반환
		if (uri.startsWith("/api/oauth2/authorization") ||
			uri.startsWith("/api/login/oauth2/code") ||
			uri.equals("/error")) {
			log.info("OAuth2 관련 경로 감지, 기본 인증 흐름으로 위임: {}", uri);
			// 기본 AuthenticationEntryPoint로 위임
			new LoginUrlAuthenticationEntryPoint("/api/oauth2/authorization/naver")
				.commence(request, response, authException);
			return;
		}

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(ErrorCode.AUTHENTICATION_FAILED.getHttpStatus());

		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(
			ExceptionResponseDto.of(ErrorCode.AUTHENTICATION_FAILED.getHttpStatus(),
				ErrorCode.AUTHENTICATION_FAILED.getMessage(),
			null)));
	}
}
