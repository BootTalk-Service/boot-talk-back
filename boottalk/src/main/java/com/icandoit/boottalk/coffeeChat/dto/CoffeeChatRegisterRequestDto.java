package com.icandoit.boottalk.coffeeChat.dto;

import jakarta.validation.Valid;

public record CoffeeChatRegisterRequestDto(
    @Valid CoffeeChatInfoRequestDto info,
    @Valid CoffeeChatTimeMapDto time
) {

}