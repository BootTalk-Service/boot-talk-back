package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentoType;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coffee-chats/info")
@RequiredArgsConstructor
public class CoffeeChatQueryController {
    private final CoffeeChatQueryService coffeeChatQueryService;

    @GetMapping("/search")
    public ResponseEntity<Page<CoffeeChatListDto>> getCoffeeChats(
        @RequestParam(required = false) JobType jobType,
        @RequestParam(required = false) MentoType mentoType,
        @PageableDefault(
            sort = "CREATED_AT",
            direction = Sort.Direction.DESC
        ) Pageable pageable
    ) {
        return ResponseEntity.ok(
            coffeeChatQueryService.getFilteredCoffeeChatResults(jobType, mentoType, pageable)
        );
    }
}
