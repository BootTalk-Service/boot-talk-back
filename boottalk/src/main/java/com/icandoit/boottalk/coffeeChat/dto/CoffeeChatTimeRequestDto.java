package com.icandoit.boottalk.coffeeChat.dto;

import java.util.List;

public record CoffeeChatTimeRequestDto(
    List<CoffeeChatTimeDto> availableTimes
) {}