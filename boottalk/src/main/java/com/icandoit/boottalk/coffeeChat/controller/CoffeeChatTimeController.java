package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatTimeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coffee-chats/times")
@RequiredArgsConstructor
public class CoffeeChatTimeController {
    private final CoffeeChatTimeService coffeeChatTimeService;

    @PostMapping("/available-times")
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> createCoffeeChatTimes(
        @RequestBody @Valid CoffeeChatTimeRequestDto requestDto) {
        // todo : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatTimeService.createCoffeeChatTimes(userId, requestDto));
    }
}
