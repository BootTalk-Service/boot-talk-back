package com.icandoit.boottalk.social_login.config.exception;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icandoit.boottalk.libs.dto.ExceptionResponseDto;
import com.icandoit.boottalk.libs.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAccessDeniedException implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		log.warn("사용자 권한 없음 : {}, URI: {}", accessDeniedException.getMessage(), request.getRequestURI());

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(ErrorCode.FORBIDDEN.getHttpStatus());

		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(
			ExceptionResponseDto.of(ErrorCode.FORBIDDEN.getHttpStatus(),
				ErrorCode.FORBIDDEN.getMessage(),
				null)
		));
	}
}
