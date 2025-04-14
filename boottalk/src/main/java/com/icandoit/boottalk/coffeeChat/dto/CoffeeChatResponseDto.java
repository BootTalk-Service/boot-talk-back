package com.icandoit.boottalk.coffeeChat.dto;

import java.util.List;

public record CoffeeChatResponseDto(
    CoffeeChatInfoResponseDto info,
    List<CoffeeChatTimeResponseDto> times
) {

    public static CoffeeChatResponseDto of(CoffeeChatInfoResponseDto info, List<CoffeeChatTimeResponseDto> times) {
        return new CoffeeChatResponseDto(info, times);
    }
}