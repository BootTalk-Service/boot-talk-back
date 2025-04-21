package com.icandoit.boottalk.social_login.oauth2;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.social_login.dto.UserRole;
import com.icandoit.boottalk.social_login.jwt.JwtProvider;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtProvider jwtProvider;

	@Value("${server.url}")
	private String BASE_URL;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		//CustomOAuth2UserService.loadUser 에서 반환된 사용자 정보 가져오기
		CustomOAuth2User userDetails = (CustomOAuth2User) authentication.getPrincipal();
		log.error("소셜 로그인 성공 userDetails info : {}, {}, {}", userDetails.getServiceUserId(), userDetails.getName(), userDetails.getAuthorities());

		//사용자 권한 가져오기
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();
		log.info("role : {}", role);


		//토큰 생성 후 쿠키에 담아 전달
		// response.addCookie(createCookie("Authorization", jwtProvider.createToken(userDetails.getServiceUserId(), userDetails.getName(), role)));
		String token = jwtProvider.createToken(userDetails.getServiceUserId(), userDetails.getName(), role);
		createCookie(response ,"Authorization"
			, token);

		// 신규회원인 경우,추가정보 입력 url로 리다이렉션
		if (UserRole.valueOf(role).equals(UserRole.NEW_USER)) {
			response.sendRedirect(BASE_URL + "/social-register");
		} else {
			// 기존 회원의 경우, 메인페이지로 리다이렉션
			response.sendRedirect(BASE_URL);
		}
	}

	private void createCookie(HttpServletResponse response, String key, String value) {

		ResponseCookie responseCookie = ResponseCookie.from(key, value)
			.maxAge(60 * 60 * 60)
			.path("/")
			.httpOnly(true)
			.secure(false)
			.sameSite("Lax")
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
	}
}
