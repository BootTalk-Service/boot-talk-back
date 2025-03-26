package com.icandoit.boottalk.coffeeChat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoffeeChatInfoRequestDto {

    public String userType;
    public String jobType;
    public String introduction;

    public static CoffeeChatInfoRequestDto of(String userType, String jobType, String introduction) {
        return new CoffeeChatInfoRequestDto(userType,jobType,introduction);
    }
}
