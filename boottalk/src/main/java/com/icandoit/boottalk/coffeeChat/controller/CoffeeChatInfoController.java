package com.icandoit.boottalk.coffeeChat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatInfoService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coffee-chats/info")
@RequiredArgsConstructor
public class CoffeeChatInfoController {

    private final CoffeeChatInfoService coffeeChatInfoService;

    @GetMapping
    public ResponseEntity<CoffeeChatInfoResponseDto> getMyCoffeeChatInfo(@AuthenticationPrincipal CustomOAuth2User user) {

        return ResponseEntity.ok(coffeeChatInfoService.getMyCoffeeChatInfo(user.getServiceUserId()));
    }

    @PutMapping
    public ResponseEntity<CoffeeChatInfoResponseDto> updateCoffeeChatInfo(
        @AuthenticationPrincipal CustomOAuth2User user,
        @RequestBody CoffeeChatInfoRequestDto requestDto) {
        return ResponseEntity.ok(coffeeChatInfoService.updateMyCoffeeChatInfo(user.getServiceUserId(), requestDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCoffeeChatInfo(@AuthenticationPrincipal CustomOAuth2User user) {
        coffeeChatInfoService.deleteMyCoffeeChatInfo(user.getServiceUserId());
        return ResponseEntity.ok().build();
    }
}
