package com.icandoit.boottalk.coffeeChat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationUpdateDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coffee-chats/applications")
@RequiredArgsConstructor
public class CoffeeChatApplicationController {

    private final CoffeeChatApplicationService coffeeChatAppService;

    // 커피챗 신청 등록
    @PostMapping
    public ResponseEntity<CoffeeChatApplicationResponseDto> createCoffeeChatApp(
        @Valid @RequestBody CoffeeChatApplicationCreateDto request
    ) {
        // TODO : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatAppService.createCoffeeChatApp(userId, request));
    }

    // 나의 커피챗 신청 목록 조회
    @GetMapping
    public ResponseEntity<List<CoffeeChatApplicationResponseDto>> getMyCoffeeChatApps() {
        // TODO : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatAppService.getMyCoffeeChatApps(userId));
    }
    
    // 커피챗 신청 상세 조회
    @GetMapping("/{coffeeChatAppId}")
    public ResponseEntity<CoffeeChatApplicationResponseDto> getCoffeeChatApp(@PathVariable Long coffeeChatAppId) {
        return ResponseEntity.ok(coffeeChatAppService.getCoffeeChatApp(coffeeChatAppId));
    }

    // 커피챗 신청 수정
    @PutMapping("/{coffeeChatAppId}")
    public ResponseEntity<CoffeeChatApplicationResponseDto> updateCoffeeChatApp(
        @PathVariable Long coffeeChatAppId,
        @Valid @RequestBody CoffeeChatApplicationUpdateDto request) {
        // TODO : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatAppService.updateCoffeeChatApp(userId, coffeeChatAppId, request));
    }

    // 커피챗 신청 삭제
    @DeleteMapping("/{coffeeChatAppId}")
    public ResponseEntity<Void> deleteCoffeeChatApp(@PathVariable Long coffeeChatAppId) {
        // TODO : 사용자 인증 사용 시 수정
        Long userId = 1L;
        coffeeChatAppService.deleteCoffeeChatApp(userId, coffeeChatAppId);
        return ResponseEntity.ok().build();
    }

    // TODO: 커피챗 신청 예약 일정 조회

}
