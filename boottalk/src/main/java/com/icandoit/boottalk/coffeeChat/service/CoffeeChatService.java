package com.icandoit.boottalk.coffeeChat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatService {

    private final CoffeeChatInfoService coffeeChatInfoService;
    private final CoffeeChatTimeService coffeeChatTimeService;

    @Transactional
    public CoffeeChatResponseDto register(Long userId, CoffeeChatRequestDto requestDto) {
        return CoffeeChatResponseDto.of(
            // 1. 커피챗 정보 등록
            coffeeChatInfoService.createCoffeeChatInfo(userId, requestDto.info()),
            // 2. 커피챗 시간 등록
            coffeeChatTimeService.createCoffeeChatTimes(userId, requestDto.time())
        );
    }

    public CoffeeChatResponseDto getMyCoffeeChat(Long userId) {
        return CoffeeChatResponseDto.of(
            coffeeChatInfoService.getMyCoffeeChatInfo(userId),
            coffeeChatTimeService.getMentorAvailableChatTimes(userId)
        );
    }

    public CoffeeChatResponseDto getCoffeeChat(Long coffeeChatInfoId) {
        CoffeeChatInfoResponseDto infoDto = coffeeChatInfoService.getCoffeeChatInfo(coffeeChatInfoId);
        return CoffeeChatResponseDto.of(
            infoDto,
            coffeeChatTimeService.getMentorAvailableChatTimes(infoDto.mentorUserId())
        );
    }

    public CoffeeChatResponseDto updateCoffeeChat(Long userId, CoffeeChatRequestDto requestDto) {
        return CoffeeChatResponseDto.of(
            coffeeChatInfoService.updateMyCoffeeChatInfo(userId, requestDto.info()),
            coffeeChatTimeService.updateCoffeeChatTimes(userId, requestDto.time())
        );
    }

    public void deleteCoffeeChat(Long userId) {
        coffeeChatInfoService.deleteMyCoffeeChatInfo(userId);
        // 커피챗 정보(CoffeeChatInfo)를 삭제하면, 설정된 cascade 옵션에 따라 연관된 시간 정보(CoffeeChatTime)도 자동 삭제됨

    }

}