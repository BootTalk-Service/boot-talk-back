package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoffeeChatInfoResponseDto {

    private Long coffeeChatInfoId;
    private String userType;
    private String jobType;
    private String introduction;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static CoffeeChatInfoResponseDto from(CoffeeChatInfo coffeeChatInfo) {
        return CoffeeChatInfoResponseDto.builder()
            .coffeeChatInfoId(coffeeChatInfo.getCoffeeChatInfoId())
            .userType(String.valueOf(coffeeChatInfo.getUserType()))
            .jobType(String.valueOf(coffeeChatInfo.getJobType()))
            .introduction(coffeeChatInfo.getIntroduction())
            .build();
    }
}
