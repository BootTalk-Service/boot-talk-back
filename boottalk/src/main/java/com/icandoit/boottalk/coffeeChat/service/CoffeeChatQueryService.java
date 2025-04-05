package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatQueryRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatQueryService {
    private final CoffeeChatQueryRepository coffeeChatQueryRepository;
    @Transactional(readOnly = true)
    public Page<CoffeeChatListDto> getFilteredCoffeeChatResults(
        @Nullable JobType jobType,
        @Nullable MentorType userType,
        Pageable pageable
    ) {
        return coffeeChatQueryRepository.getFilteredCoffeeChatResults(jobType, userType, pageable);
    }
}
