package com.icandoit.boottalk.coffeeChat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatAppChangeStatusDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatAppStatusResponseDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatReceivedService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coffee-chats/applications/received")
@RequiredArgsConstructor
public class CoffeeChatReceivedController {

    private final CoffeeChatReceivedService coffeeChatReceivedService;

    // 수신된 커피챗 신청 목록 조회 (멘티가 나에게 신청한 커피챗 신청 목록 조회)
    @GetMapping
    public ResponseEntity<List<CoffeeChatApplicationResponseDto>> getReceivedCoffeeChatApplications(
    ) {
        // TODO : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatReceivedService.getReceivedCoffeeChatApplications(userId));
    }

    // 커피챗 신청 상태 변경 (멘티가 나에게 신청한 커피챗 신청에 대한 상태 변경 처리 요청)
    @PutMapping("/{coffeeChatAppId}/status")
    public ResponseEntity<CoffeeChatAppStatusResponseDto> changeCoffeeChatAppStatus(
        @PathVariable Long coffeeChatAppId,
        @Valid @RequestBody CoffeeChatAppChangeStatusDto request
    ) {
        // TODO : 사용자 인증 사용 시 수정
        Long userId = 1L;
        return ResponseEntity.ok(coffeeChatReceivedService.changeCoffeeChatAppStatus(userId, coffeeChatAppId, request));
    }


}
