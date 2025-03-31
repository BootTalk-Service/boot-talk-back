package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatTimeRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoffeeChatTimeService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final CoffeeChatTimeRepository coffeeChatTimeRepository;

    public List<CoffeeChatTimeResponseDto> createCoffeeChatTimes(Long userId,
        @Valid CoffeeChatTimeRequestDto requestDto) {

        // todo: 유저조회
        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new CustomException(
                ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));

        List<CoffeeChatTime> coffeeChatTimes = new ArrayList<>();
        for (CoffeeChatTimeDto timeDto : requestDto.availableTimes()) {
            CoffeeChatTime time = CoffeeChatTime.of(
                coffeeChatInfo,
                timeDto.dayOfWeek(),
                timeDto.startTime(),
                timeDto.endTime()
            );
            coffeeChatTimes.add(time);
        }

        List<CoffeeChatTime> savedTimes = coffeeChatTimeRepository.saveAll(coffeeChatTimes);

        List<CoffeeChatTimeResponseDto> responseDtos = savedTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .collect(Collectors.toList());

        return responseDtos;
    }
}
