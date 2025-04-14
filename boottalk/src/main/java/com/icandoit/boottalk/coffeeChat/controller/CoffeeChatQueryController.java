package com.icandoit.boottalk.coffeeChat.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatQueryService;
import com.icandoit.boottalk.common.dto.PagedResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coffee-chats/info")
@RequiredArgsConstructor
public class CoffeeChatQueryController {
    private final CoffeeChatQueryService coffeeChatQueryService;

    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<CoffeeChatListDto>> getCoffeeChats(
        @RequestParam(required = false) JobType jobType,
        @RequestParam(required = false) MentorType mentorType,
        @PageableDefault(
            sort = "CREATED_AT",
            direction = Sort.Direction.DESC
        ) Pageable pageable
    ) {
        return ResponseEntity.ok(
            coffeeChatQueryService.getFilteredCoffeeChatResults(jobType, mentorType, pageable)
        );
    }
}
