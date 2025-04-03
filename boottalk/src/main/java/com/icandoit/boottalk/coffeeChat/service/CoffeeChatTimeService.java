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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatTimeService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final CoffeeChatTimeRepository coffeeChatTimeRepository;

    @Transactional
    public List<CoffeeChatTimeResponseDto> createCoffeeChatTimes(Long userId,
        @Valid CoffeeChatTimeRequestDto requestDto) {

        // todo: 유저조회
        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByMento_UserId(userId)
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
            coffeeChatInfo.addAvailableTime(time);
            coffeeChatTimes.add(time);
        }

        coffeeChatTimeRepository.saveAll(coffeeChatTimes);

        return coffeeChatTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CoffeeChatTimeResponseDto> getMyCoffeeChatTimes(Long userId) {
        List<CoffeeChatTime> coffeeChatTimes = coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(
            userId);

        if (coffeeChatTimes.isEmpty()) {
            throw new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND);
        }

        return coffeeChatTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .collect(Collectors.toList());
    }
}
