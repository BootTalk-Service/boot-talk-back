package com.icandoit.boottalk.coffeeChat.service.converter;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeMapDto;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoffeeChatTimeConverter {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static List<CoffeeChatTimeDto> toDtoList(CoffeeChatTimeMapDto mapDto) {
        List<CoffeeChatTimeDto> result = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : mapDto.availableTimes().entrySet()) {
            DayOfWeek day;
            try {
                day = DayOfWeek.valueOf(entry.getKey());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_DAY_FORMAT);
            }

            for (String timeStr : entry.getValue()) {
                try {
                    LocalTime time = LocalTime.parse(timeStr, TIME_FORMATTER);
                    result.add(new CoffeeChatTimeDto(day, time));
                } catch (DateTimeParseException e) {
                    throw new CustomException(ErrorCode.INVALID_TIME_FORMAT);
                }
            }
        }
        return result;
    }
}