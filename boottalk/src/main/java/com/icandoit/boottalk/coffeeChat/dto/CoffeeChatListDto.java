package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;

public record CoffeeChatListDto(
    Long coffeeChatInfoId,
    Long mentorUserId,
    String mentorName,
    MentorType mentorType,
    JobType jobType,
    String introduction
) {

}