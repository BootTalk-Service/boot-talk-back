package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatRegisterRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatRegisterResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatRegisterService {

    private final CoffeeChatInfoService coffeeChatInfoService;
    private final CoffeeChatTimeService coffeeChatTimeService;

    @Transactional
    public CoffeeChatRegisterResponseDto register(Long userId, CoffeeChatRegisterRequestDto requestDto) {

        // 1. 커피챗 정보 등록
        CoffeeChatInfoResponseDto infoDto = coffeeChatInfoService.createCoffeeChatInfo(
            userId, requestDto.info());

        // 2. 커피챗 시간 등록
        List<CoffeeChatTimeResponseDto> timeDtos = coffeeChatTimeService.createCoffeeChatTimes(
            userId, requestDto.time());

        return CoffeeChatRegisterResponseDto.of(infoDto, timeDtos);
    }
}