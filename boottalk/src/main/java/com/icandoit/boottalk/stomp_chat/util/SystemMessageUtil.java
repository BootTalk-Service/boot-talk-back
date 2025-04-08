package com.icandoit.boottalk.stomp_chat.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemMessageUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M월 d일 HH:mm");

    public static String createSystemMessage(LocalDateTime startTime) {
        String formattedStartTime = startTime.format(FORMATTER);
        return String.format("채팅방이 예약되었습니다. 멘토링은 %s에 시작됩니다.", formattedStartTime);
    }
}