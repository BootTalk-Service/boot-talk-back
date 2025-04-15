package com.icandoit.boottalk.coffeeChat.dto;

import java.util.List;
import java.util.Map;

public record CoffeeChatResponseDto(
    CoffeeChatInfoResponseDto info,
    Map<String, List<String>> time
) {

    public static CoffeeChatResponseDto of(CoffeeChatInfoResponseDto info, Map<String, List<String>> time) {
        return new CoffeeChatResponseDto(info, time);
    }
}