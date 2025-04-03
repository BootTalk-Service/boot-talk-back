package com.icandoit.boottalk.coffeeChat.dto;

import java.time.LocalDateTime;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;

public record CoffeeChatApplicationResponseDto(
    Long coffeeChatAppId,
    Long coffeeChatInfoId,
    Long applierUserId,
    String applierName,
    StatusType status,
    String content,
    LocalDateTime coffeeChatStartTime,
    LocalDateTime coffeeChatEndTime,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CoffeeChatApplicationResponseDto from(CoffeeChatApplication coffeeChatApp) {
        return new CoffeeChatApplicationResponseDto(
            coffeeChatApp.getCoffeeChatAppId(),
            coffeeChatApp.getCoffeeChatInfo().getCoffeeChatInfoId(),
            coffeeChatApp.getApplier().getUserId(),
            coffeeChatApp.getApplier().getUserName(),
            coffeeChatApp.getStatus(),
            coffeeChatApp.getContent(),
            coffeeChatApp.getCoffeeChatStartTime(),
            coffeeChatApp.getCoffeeChatEndTime(),
            coffeeChatApp.getCreatedAt(),
            coffeeChatApp.getUpdatedAt()
        );
    }
}
