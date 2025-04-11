package com.icandoit.boottalk.coffeeChat.dto;

import java.util.List;

public record CoffeeChatRegisterResponseDto(
    CoffeeChatInfoResponseDto info,
    List<CoffeeChatTimeResponseDto> times
) {

    public static CoffeeChatRegisterResponseDto of(CoffeeChatInfoResponseDto info, List<CoffeeChatTimeResponseDto> times) {
        return new CoffeeChatRegisterResponseDto(info, times);
    }
}