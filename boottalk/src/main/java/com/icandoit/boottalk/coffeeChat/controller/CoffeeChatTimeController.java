package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeListDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeMapDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatTimeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        @RequestBody @Valid CoffeeChatTimeMapDto requestDto) {
        // todo : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatTimeService.createCoffeeChatTimes(userId, requestDto));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> getMyCoffeeChatTimes() {
        Long userId = 1L;

        return ResponseEntity.ok(coffeeChatTimeService.getMyCoffeeChatTimes(userId));
    }

    @PutMapping("/my")
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> updateCoffeeChatTimes(
        @RequestBody @Valid CoffeeChatTimeListDto requestDto) {
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatTimeService.updateCoffeeChatTimes(userId, requestDto));
    }
}
