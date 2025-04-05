package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeMapDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatTimeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> createCoffeeChatTimes(
        @RequestBody @Valid CoffeeChatTimeMapDto requestDto) {
        // todo : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatTimeService.createCoffeeChatTimes(userId, requestDto));
    }

    // 멘토 자신의 시간을 조회
    @GetMapping
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> getMyCoffeeChatTimes() {
        Long userId = 1L;

        return ResponseEntity.ok(coffeeChatTimeService.getMentorAvailableChatTimes(userId));
    }

    //mentorId를 받아 커피챗 가능 시간 조회
    @GetMapping("/{mentorId}")
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> getMentorAvailableChatTimes(@PathVariable Long mentorId) {

        return ResponseEntity.ok(coffeeChatTimeService.getMentorAvailableChatTimes(mentorId));
    }

    @PutMapping
    public ResponseEntity<List<CoffeeChatTimeResponseDto>> updateCoffeeChatTimes(
        @RequestBody @Valid CoffeeChatTimeMapDto requestDto) {
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatTimeService.updateCoffeeChatTimes(userId, requestDto));
    }
}
