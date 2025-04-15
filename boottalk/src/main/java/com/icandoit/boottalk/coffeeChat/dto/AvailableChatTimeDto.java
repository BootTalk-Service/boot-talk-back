package com.icandoit.boottalk.coffeeChat.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record AvailableChatTimeDto (
    Map<LocalDate, List<String>> availableChatTimes
){
}