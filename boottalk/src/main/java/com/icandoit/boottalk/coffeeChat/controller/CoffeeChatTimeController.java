package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.AvailableChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeMapDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatTimeService;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coffee-chats/times")
@RequiredArgsConstructor
public class CoffeeChatTimeController {

    private final CoffeeChatTimeService coffeeChatTimeService;

    // 멘토 자신의 시간을 조회
    @GetMapping
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> getMyCoffeeChatTimes(@AuthenticationPrincipal CustomOAuth2User user) {

        return ResponseEntity.ok(coffeeChatTimeService.getMentorAvailableChatTimes(user.getServiceUserId()));
    }

    //mentorId를 받아 커피챗 가능 시간 조회
    @GetMapping("/{coffeeChatInfoId}")
    public ResponseEntity<AvailableChatTimeDto> getAvailableChatTimes(@PathVariable Long coffeeChatInfoId) {
        return ResponseEntity.ok(coffeeChatTimeService.getAvailableChatTimes(coffeeChatInfoId));
    }

    @PutMapping
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> updateCoffeeChatTimes(
        @AuthenticationPrincipal CustomOAuth2User user,
        @RequestBody @Valid CoffeeChatTimeMapDto requestDto) {
        return ResponseEntity.ok(coffeeChatTimeService.updateCoffeeChatTimes(user.getServiceUserId(), requestDto));
    }
}
