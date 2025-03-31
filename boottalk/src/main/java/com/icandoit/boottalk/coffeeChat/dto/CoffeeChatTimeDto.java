package com.icandoit.boottalk.coffeeChat.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CoffeeChatTimeDto(
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
) {

}
