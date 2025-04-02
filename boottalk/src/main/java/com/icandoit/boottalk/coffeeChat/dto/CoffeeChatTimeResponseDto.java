package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record CoffeeChatTimeResponseDto(
    Long coffeeChatTimeId,
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
) {

    public static CoffeeChatTimeResponseDto from(CoffeeChatTime coffeeChatTime) {
        return new CoffeeChatTimeResponseDto(
            coffeeChatTime.getCoffeeChatTimeId(),
            coffeeChatTime.getDayOfWeek(),
            coffeeChatTime.getStartTime(),
            coffeeChatTime.getEndTime()
        );
    }
}