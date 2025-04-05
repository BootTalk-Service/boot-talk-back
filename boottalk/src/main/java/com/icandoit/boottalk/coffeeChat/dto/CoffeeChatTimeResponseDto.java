package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public record CoffeeChatTimeResponseDto(
    Long coffeeChatTimeId,
    DayOfWeek dayOfWeek,
    String startTime
) {
    public static CoffeeChatTimeResponseDto from(CoffeeChatTime coffeeChatTime) {
        return new CoffeeChatTimeResponseDto(
            coffeeChatTime.getCoffeeChatTimeId(),
            coffeeChatTime.getDayOfWeek(),
            coffeeChatTime.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
    }
}