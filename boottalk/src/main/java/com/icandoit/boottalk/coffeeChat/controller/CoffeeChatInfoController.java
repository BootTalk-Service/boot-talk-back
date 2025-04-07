package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coffee-chats/info")
@RequiredArgsConstructor
public class CoffeeChatInfoController {

    private final CoffeeChatInfoService coffeeChatInfoService;


    @PostMapping
    public ResponseEntity<CoffeeChatInfoResponseDto> createCoffeeChatInfo(
        @RequestBody @Valid CoffeeChatInfoRequestDto requestDto
    ) {
        // todo : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatInfoService.createCoffeeChatInfo(userId, requestDto));
    }

    @GetMapping
    public ResponseEntity<CoffeeChatInfoResponseDto> getMyCoffeeChatInfo() {
        // todo : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatInfoService.getMyCoffeeChatInfo(userId));
    }

    @PutMapping
    public ResponseEntity<CoffeeChatInfoResponseDto> updateCoffeeChatInfo(
        @RequestBody CoffeeChatInfoRequestDto requestDto) {
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatInfoService.updateMyCoffeeChatInfo(userId, requestDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCoffeeChatInfo() {
        Long userId = 1L;
        coffeeChatInfoService.deleteMyCoffeeChatInfo(userId);
        return ResponseEntity.ok().build();
    }
}
