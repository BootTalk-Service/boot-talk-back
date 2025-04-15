package com.icandoit.boottalk.coffeeChat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatQueryRepository;
import com.icandoit.boottalk.common.dto.PagedResponseDto;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatQueryService {
    private final CoffeeChatQueryRepository coffeeChatQueryRepository;
    @Transactional(readOnly = true)
    public PagedResponseDto<CoffeeChatListDto> getFilteredCoffeeChatResults(
        @Nullable JobType jobType,
        @Nullable MentorType userType,
        Pageable pageable
    ) {
        return coffeeChatQueryRepository.getFilteredCoffeeChatResults(jobType, userType, pageable);
    }
}
