package com.icandoit.boottalk.coffeeChat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoffeeChatListDto {
    private Long coffeeChatInfoId;
    private Long userId;
    private String userName;
    private String userType;
    private String jobType;
    private String introduction;
}