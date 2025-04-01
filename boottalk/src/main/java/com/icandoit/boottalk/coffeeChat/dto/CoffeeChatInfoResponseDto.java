package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import java.time.LocalDateTime;


public record CoffeeChatInfoResponseDto(
    Long coffeeChatInfoId,
    Long userId,
    String userName,
    String userType,
    String jobType,
    String introduction,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static CoffeeChatInfoResponseDto from(CoffeeChatInfo coffeeChatInfo) {
        return new CoffeeChatInfoResponseDto(
            coffeeChatInfo.getCoffeeChatInfoId(),
            coffeeChatInfo.getUser().getUserId(),
            coffeeChatInfo.getUser().getName(),
            coffeeChatInfo.getUserType().name(),
            coffeeChatInfo.getJobType().name(),
            coffeeChatInfo.getIntroduction(),
            coffeeChatInfo.getCreatedAt(),
            coffeeChatInfo.getUpdatedAt()
        );
    }
}
