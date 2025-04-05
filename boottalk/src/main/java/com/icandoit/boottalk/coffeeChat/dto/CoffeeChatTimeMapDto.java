package com.icandoit.boottalk.coffeeChat.dto;

import java.util.List;
import java.util.Map;

public record CoffeeChatTimeMapDto(
    Map<String, List<String>> availableTimes
) {
}