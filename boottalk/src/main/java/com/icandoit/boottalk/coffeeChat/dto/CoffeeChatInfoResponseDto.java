package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.UserType;
import java.time.LocalDateTime;


public record CoffeeChatInfoResponseDto(
    Long coffeeChatInfoId,
    Long userId,
    String userName,
    UserType userType,
    JobType jobType,
    String introduction,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static CoffeeChatInfoResponseDto from(CoffeeChatInfo coffeeChatInfo) {
        return new CoffeeChatInfoResponseDto(
            coffeeChatInfo.getCoffeeChatInfoId(),
            coffeeChatInfo.getUser().getUserId(),
            coffeeChatInfo.getUser().getUserName(),
            coffeeChatInfo.getUserType(),
            coffeeChatInfo.getJobType(),
            coffeeChatInfo.getIntroduction(),
            coffeeChatInfo.getCreatedAt(),
            coffeeChatInfo.getUpdatedAt()
        );
    }
}
