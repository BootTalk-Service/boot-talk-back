package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import java.time.LocalDateTime;

public record CoffeeChatInfoApprovedDto(
    Long coffeeChatAppId,
    Long coffeeChatInfoId,
    Long menteeUserId,
    String menteeName,
    String mentoName,
    StatusType status,
    String content,
    LocalDateTime coffeeChatStartTime,
    LocalDateTime coffeeChatEndTime
) {
    public static CoffeeChatInfoApprovedDto from(CoffeeChatApplication application) {
        return new CoffeeChatInfoApprovedDto(
            application.getCoffeeChatAppId(),
            application.getCoffeeChatInfo().getCoffeeChatInfoId(),
            application.getMentee().getUserId(),
            application.getMentee().getUserName(),
            application.getCoffeeChatInfo().getMento().getUserName(),
            application.getStatus(),
            application.getContent(),
            application.getCoffeeChatStartTime(),
            application.getCoffeeChatEndTime()
        );
    }
}