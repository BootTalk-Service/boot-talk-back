package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.UserType;

public record CoffeeChatListDto(
    Long coffeeChatInfoId,
    Long userId,
    String userName,
    UserType userType,
    JobType jobType,
    String introduction
) {

}