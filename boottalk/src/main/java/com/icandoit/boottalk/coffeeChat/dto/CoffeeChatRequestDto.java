package com.icandoit.boottalk.coffeeChat.dto;

import jakarta.validation.Valid;

public record CoffeeChatRequestDto(
    @Valid CoffeeChatInfoRequestDto info,
    @Valid CoffeeChatTimeMapDto time
) {

}