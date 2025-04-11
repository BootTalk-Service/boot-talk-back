package com.icandoit.boottalk.coffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record AvailableChatTimeDto (
    Map<LocalDate, List<LocalTime>> availableChatTimes
){
}