package com.icandoit.boottalk.coffeeChat.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CoffeeChatTimeListDto(
    @NotNull
    List<CoffeeChatTimeDto> availableTimes
) {}