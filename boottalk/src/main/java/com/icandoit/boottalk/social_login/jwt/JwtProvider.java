package com.icandoit.boottalk.social_login.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.social_login.dto.UserAuthDto;
import com.icandoit.boottalk.social_login.dto.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DecodingException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider {

	private final SecretKey secretKey;

	//토큰 유효시간
	private static final long tokenValidTime = 1000L * 60 * 60 * 24;

	public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	//회원 토큰
	//TODO 사용자 정보를 암호화하여 토큰에 저장
	public String createToken(Long serviceUserId, String resourceUserId, String userRole) {

		return Jwts.builder()
			.claim("serviceUserId", serviceUserId.toString())
			.claim("resourceUserId", resourceUserId)
			.claim("role", userRole)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + tokenValidTime))
			.signWith(secretKey)
			.compact();
	}




	// 토큰 파싱 후 SecurityContextHolder 에 저장될 사용자 정보 반환
	public Authentication getAuthentication(String token) {

		OAuth2User oAuth2User = new CustomOAuth2User(getUserFromToken(token));

		return new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
	}


	//토큰 파싱과 동시에 유효 토큰 검증
	//TODO 커스텀 에러 적용
	private Claims parseValidateToken(String token) {
		try {

			log.info("Parsing token : {}", token);
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();

		} catch (ExpiredJwtException e) {
			log.error("Token expired: {}", e.getMessage());
			return e.getClaims();
		} catch (DecodingException e) {
			log.error("Decoding error: {}", e.getMessage());
			throw new RuntimeException("Invalid token format", e);
		} catch (Exception e) {
			log.error("Unknown error during token parsing: {}", e.getMessage());
			throw new CustomException(ErrorCode.TOKEN_PARSING_ERROR);
		}
	}

	private UserAuthDto getUserFromToken(String token) {

		Claims claims = parseValidateToken(token);

		return UserAuthDto.builder()
			.serviceUserId(
				Long.valueOf(Objects.requireNonNull(
					claims.get("serviceUserId", String.class))))
			.resourceUserId(claims.get("resourceUserId", String.class))
			.role(UserRole.valueOf(claims.get("role", String.class)))
			.build();
	}
}
