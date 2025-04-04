package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;

public record CoffeeChatAppStatusResponseDto(
    StatusType status
) {
    public static CoffeeChatAppStatusResponseDto from(CoffeeChatApplication coffeeChatApp) {
        return new CoffeeChatAppStatusResponseDto(
            coffeeChatApp.getStatus()
        );
    }
}
