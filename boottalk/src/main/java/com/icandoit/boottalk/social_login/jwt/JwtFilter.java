package com.icandoit.boottalk.social_login.jwt;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// swagger 접근 시 필터 적용 X
		String requestURI = request.getRequestURI();

		if (requestURI.startsWith("/swagger-ui/")
			|| requestURI.startsWith("/v3/api-docs"))
		{
			filterChain.doFilter(request, response);
			return;
		}


		// 처음 로그인하고 나서 리다이렉트 된 url에서는 토큰이 헤더에 담겨져 있지 않고 쿠키에 담겨 있음.

		String token = null;

		// 먼저 헤더에서 토큰 확인
		token = tokenFromHeader(request);

		// 리다이렉션 후에는 헤더에 토큰이 없기 때문에 쿠키에서 가져와 응답 헤더에 옮기고 그 후 쿠키에 있는 토큰값 삭제
		if (token == null) {
			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(TOKEN_HEADER)) {
						token = cookie.getValue();

						// 쿠키에서 찾은 토큰을 다음 요청부터는 헤더에서 사용할 수 있도록
						// 응답 헤더에 토큰 추가
						response.setHeader(TOKEN_HEADER, TOKEN_PREFIX + token);

						// TODO 추후 프론트 엔드에서 헤더에 토큰 값을 담아서 요청을 보내도록 설정 시 쿠키 삭제 활성화
						// Cookie deleteCookie = new Cookie(TOKEN_HEADER, null);
						// deleteCookie.setMaxAge(0); // 쿠키 만료
						// deleteCookie.setPath("/"); // 쿠키의 경로를 원래 쿠키와 동일하게 설정
						// response.addCookie(deleteCookie);

						break;
					}
				}
			}
		}

		// 토큰이 아예 없는 경우
		if (token == null) {
			log.warn("Not Found Authorization Token");
			filterChain.doFilter(request, response);
			return;
		}

		// 토큰에 담겨있는 사용자 정보를 SecurityContextHolder 에 저장
		SecurityContextHolder.getContext().setAuthentication(jwtProvider.getAuthentication(token));

		//TODO 어떤 사용자 정보를 가지고 있는지 로그를 통해 확인. 추후 사용자 ID 정도만 확인할 수 있도록 변경
		CustomOAuth2User userDetails =  (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		log.info("token info : {}, {}, {}", userDetails.getName(), userDetails.getAuthorities().stream().map(
			GrantedAuthority::getAuthority).collect(Collectors.toList()), userDetails.getServiceUserId());

		filterChain.doFilter(request, response);
	}

	private String tokenFromHeader(HttpServletRequest request) {
		String bearerToken = request.getHeader(TOKEN_HEADER);
		log.info("bearer token {}", bearerToken);

		if (!StringUtils.hasText(bearerToken)) {
			return null;
		}

		// 토큰 형식이 잘못된 경우 예외 발생
		if (!bearerToken.startsWith(TOKEN_PREFIX)) {
			throw new CustomException(INVALID_TOKEN);
		}

		return bearerToken.substring(TOKEN_PREFIX.length()).trim();
	}
}
