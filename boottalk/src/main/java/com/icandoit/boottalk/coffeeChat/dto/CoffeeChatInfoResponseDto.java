package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentoType;

import java.time.LocalDateTime;


public record CoffeeChatInfoResponseDto(
    Long coffeeChatInfoId,
    Long mentoUserId,
    String mentoName,
    MentoType mentoType,
    JobType jobType,
    String introduction,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static CoffeeChatInfoResponseDto from(CoffeeChatInfo coffeeChatInfo) {
        return new CoffeeChatInfoResponseDto(
            coffeeChatInfo.getCoffeeChatInfoId(),
            coffeeChatInfo.getMento().getUserId(),
            coffeeChatInfo.getMento().getUserName(),
            coffeeChatInfo.getMentoType(),
            coffeeChatInfo.getJobType(),
            coffeeChatInfo.getIntroduction(),
            coffeeChatInfo.getCreatedAt(),
            coffeeChatInfo.getUpdatedAt()
        );
    }
}
