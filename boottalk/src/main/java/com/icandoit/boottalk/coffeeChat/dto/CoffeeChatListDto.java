package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentoType;

public record CoffeeChatListDto(
    Long coffeeChatInfoId,
    Long mentoUserId,
    String mentoName,
    MentoType mentoType,
    JobType jobType,
    String introduction
) {

}