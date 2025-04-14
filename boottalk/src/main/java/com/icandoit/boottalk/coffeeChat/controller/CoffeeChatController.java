package com.icandoit.boottalk.coffeeChat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.coffeeChat.dto.AvailableChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatService;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatTimeService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coffee-chats")
@RequiredArgsConstructor
public class CoffeeChatController {

    private final CoffeeChatService coffeeChatService;
    private final CoffeeChatTimeService coffeeChatTimeService;

    @PostMapping
    public ResponseEntity<CoffeeChatResponseDto> registerCoffeeChat(
        @AuthenticationPrincipal CustomOAuth2User user,
        @RequestBody @Valid CoffeeChatRequestDto requestDto
    ) {
        return ResponseEntity.ok(coffeeChatService.register(user.getServiceUserId(),
            requestDto));
    }

    @GetMapping
    public ResponseEntity<CoffeeChatResponseDto> getMyCoffeeChat(@AuthenticationPrincipal CustomOAuth2User user) {
        return ResponseEntity.ok(coffeeChatService.getMyCoffeeChat(user.getServiceUserId()));
    }

    @GetMapping("/{coffeeChatInfoId}")
    public ResponseEntity<CoffeeChatResponseDto> getCoffeeChat(@PathVariable Long coffeeChatInfoId) {
        return ResponseEntity.ok(coffeeChatService.getCoffeeChat(coffeeChatInfoId));
    }

    @PutMapping
    public ResponseEntity<CoffeeChatResponseDto> updateCoffeeChat(
        @AuthenticationPrincipal CustomOAuth2User user,
        @RequestBody CoffeeChatRequestDto requestDto
    ) {
        return ResponseEntity.ok(coffeeChatService.updateCoffeeChat(user.getServiceUserId(), requestDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCoffeeChat(@AuthenticationPrincipal CustomOAuth2User user) {
        coffeeChatService.deleteCoffeeChat(user.getServiceUserId());
        return ResponseEntity.ok().build();
    }


    // coffeeChatInfoId를 받아 커피챗 신청 가능 시간 조회
    @GetMapping("/{coffeeChatInfoId}/times")
    public ResponseEntity<AvailableChatTimeDto> getAvailableChatTimes(@PathVariable Long coffeeChatInfoId) {
        return ResponseEntity.ok(coffeeChatTimeService.getAvailableChatTimes(coffeeChatInfoId));
    }

}