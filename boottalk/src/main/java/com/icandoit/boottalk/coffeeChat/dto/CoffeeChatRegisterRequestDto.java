package com.icandoit.boottalk.coffeeChat.dto;

public record CoffeeChatRegisterRequestDto(
    CoffeeChatInfoRequestDto info,
    CoffeeChatTimeMapDto time
) {

}