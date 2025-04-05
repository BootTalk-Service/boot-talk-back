package com.icandoit.boottalk.coffeeChat.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coffee-chats/applications/received")
@RequiredArgsConstructor
public class CoffeeChatReceivedController {

    private final CoffeeChatReceivedService coffeeChatReceivedService;

    // 수신된 커피챗 신청 목록 조회 (멘티가 나에게 신청한 커피챗 신청 목록 조회)
    @GetMapping
    public ResponseEntity<PagedResponseDto<CoffeeChatApplicationResponseDto>> getReceivedCoffeeChatApplications(
        @AuthenticationPrincipal CustomOAuth2User user,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(coffeeChatReceivedService.getReceivedCoffeeChatApplications(
            user.getServiceUserId(), pageable));
    }

    // 커피챗 신청 상태 변경 (멘티가 나에게 신청한 커피챗 신청에 대한 상태 변경 처리 요청)
    @PutMapping("/{coffeeChatAppId}/status")
    public ResponseEntity<CoffeeChatAppStatusResponseDto> changeCoffeeChatAppStatus(
        @AuthenticationPrincipal CustomOAuth2User user,
        @PathVariable Long coffeeChatAppId,
        @Valid @RequestBody CoffeeChatAppChangeStatusDto request
    ) {
        return ResponseEntity.ok(coffeeChatReceivedService.changeCoffeeChatAppStatus(
            user.getServiceUserId(), coffeeChatAppId, request));
    }


}
