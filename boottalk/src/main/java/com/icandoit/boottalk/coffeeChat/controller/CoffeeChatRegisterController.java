package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatRegisterRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatRegisterResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatRegisterService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coffee-chats")
@RequiredArgsConstructor
public class CoffeeChatRegisterController {

    private final CoffeeChatRegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<CoffeeChatRegisterResponseDto> registerCoffeeChat(
        @AuthenticationPrincipal CustomOAuth2User user,
        @RequestBody @Valid CoffeeChatRegisterRequestDto requestDto
    ) {
        return ResponseEntity.ok(registerService.register(user.getServiceUserId(),
            requestDto));
    }
}