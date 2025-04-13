package com.icandoit.boottalk.coffeeChat.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationUpdateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoApprovedDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatApplicationService;
import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;

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
        @AuthenticationPrincipal CustomOAuth2User user,
        @Valid @RequestBody CoffeeChatApplicationCreateDto request
    ) {
        return ResponseEntity.ok(coffeeChatAppService.createCoffeeChatApp(
            user.getServiceUserId(), request));
    }

    // 나의 커피챗 신청 목록 조회
    @GetMapping
    public ResponseEntity<PagedResponseDto<CoffeeChatApplicationResponseDto>> getMyCoffeeChatApps(
        @AuthenticationPrincipal CustomOAuth2User user,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(coffeeChatAppService.getMyCoffeeChatApps(
            user.getServiceUserId(), pageable));
    }

    // 나의 수락된 커피챗 목록 조회
    @GetMapping("/approved")
    public ResponseEntity<PagedResponseDto<CoffeeChatInfoApprovedDto>> getApprovedCoffeeChats(
        @AuthenticationPrincipal CustomOAuth2User user,
        Pageable pageable
    ) {
        return ResponseEntity.ok(coffeeChatAppService.getApprovedCoffeeChats(
            user.getServiceUserId(), pageable));
    }

    // 커피챗 신청 상세 조회
    @GetMapping("/{coffeeChatAppId}")
    public ResponseEntity<CoffeeChatApplicationResponseDto> getCoffeeChatAppInfo(@PathVariable Long coffeeChatAppId) {
        return ResponseEntity.ok(coffeeChatAppService.getCoffeeChatAppInfo(coffeeChatAppId));
    }

    // 커피챗 신청 수정
    @PutMapping("/{coffeeChatAppId}")
    public ResponseEntity<CoffeeChatApplicationResponseDto> updateCoffeeChatApp(
        @AuthenticationPrincipal CustomOAuth2User user,
        @PathVariable Long coffeeChatAppId,
        @Valid @RequestBody CoffeeChatApplicationUpdateDto request
    ) {
        return ResponseEntity.ok(coffeeChatAppService.updateCoffeeChatApp(
            user.getServiceUserId(), coffeeChatAppId, request));
    }

    // 커피챗 신청 취소
    @DeleteMapping("/{coffeeChatAppId}")
    public ResponseEntity<Void> cancelCoffeeChatApp(
        @AuthenticationPrincipal CustomOAuth2User user,
        @PathVariable Long coffeeChatAppId
    ) {
        coffeeChatAppService.cancelCoffeeChatApp(user.getServiceUserId(), coffeeChatAppId);
        return ResponseEntity.ok().build();
    }


}
