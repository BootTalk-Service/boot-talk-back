package com.icandoit.boottalk.social_login.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.icandoit.boottalk.social_login.jwt.JwtFilter;
import com.icandoit.boottalk.social_login.oauth2.CustomClientRegistrationRepository;
import com.icandoit.boottalk.social_login.oauth2.CustomSuccessHandler;
import com.icandoit.boottalk.social_login.service.CustomUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
public class SecurityConfiguration {

	private final CustomClientRegistrationRepository customClientRegistrationRepository;
	private final CustomSuccessHandler customSuccessHandler;
	private final CustomUserService customUserService;
	private final JwtFilter jwtFilter;


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
			.cors(cors ->  cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			// .exceptionHandling(exception -> {
			// 	log.info("Exception Handling triggered");
			// 	exception.authenticationEntryPoint((request, response, authException) -> {
			// 		log.info("Unauthorized access attempt: {}", request.getRequestURI());
			// 		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			// 	});
			// })


		// 세션 사용하지 않기 때문에
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// 경로에 대한 접근 권한 설정
				// TODO  : 추후에 접근 가능한 페이지 설정
				.requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**", "/api/oauth2/authorization/**", "/login/**", "/api/bootcamps/**", "/api/reviews/**").permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			//oauth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(endpoint -> endpoint. // 프론트엔드쪽에서 로그인 버튼을 눌렀을 때
					baseUri("/api/oauth2/authorization")) // 해당 엔드포인트로 요청을 보내면 네이버 로그인 페이지로 리디렉션
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customUserService)) // 네이버로부터 사용자 정보()를 받아올 클래스
				.successHandler(customSuccessHandler) // 로그인 성공 시 토큰을 발급하는 클래스
				.clientRegistrationRepository(customClientRegistrationRepository.clientRegistrationRepository())) // 부트톡 서버와 네이버 서버간 인증코드와 엑세스 토큰을 주고 받기 위한 설정이 저장된 클래스
			.logout(logout -> logout.logoutUrl("/logout")
				.logoutSuccessHandler((request, response, auth) -> {
					response.sendRedirect("http://localhost:3000/"); // 로그아웃 성공 시 메인페이지로 이동
				})
				.clearAuthentication(true));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
