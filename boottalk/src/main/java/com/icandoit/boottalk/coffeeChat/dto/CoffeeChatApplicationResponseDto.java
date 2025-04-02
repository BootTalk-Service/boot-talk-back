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
    private static CoffeeChatApplicationResponseDto from(
        CoffeeChatApplication coffeeChatApp,
        boolean showContent
    ) {
        return new CoffeeChatApplicationResponseDto(
            coffeeChatApp.getCoffeeChatAppId(),
            coffeeChatApp.getCoffeeChatInfo().getCoffeeChatInfoId(),
            coffeeChatApp.getApplier().getUserId(),
            coffeeChatApp.getApplier().getUserName(),
            coffeeChatApp.getStatus(),
            showContent ? coffeeChatApp.getContent() : null,
            coffeeChatApp.getCoffeeChatStartTime(),
            coffeeChatApp.getCoffeeChatEndTime(),
            coffeeChatApp.getCreatedAt(),
            coffeeChatApp.getUpdatedAt()
        );
    }

    // 목록 조회에서는 content를 포함하지 않음 (불필요한 데이터 전송 방지)
    public static CoffeeChatApplicationResponseDto fromListAll(CoffeeChatApplication coffeeChatApp) {
        return from(coffeeChatApp, false);
    }

    public static CoffeeChatApplicationResponseDto fromDetail(CoffeeChatApplication coffeeChatApp) {
        return from(coffeeChatApp, true);
    }
}
