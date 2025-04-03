package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentoType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CoffeeChatInfoRequestDto(
    @NotNull(message = "mentoType은 필수 입력값입니다.")
    MentoType mentoType,

    @NotNull(message = "jobType은 필수 입력값입니다.")
    JobType jobType,

    @NotBlank(message = "소개글은 필수 입력값입니다.")
    @Size(max = 1000, message = "소개글은 최대 1000자까지 입력할 수 있습니다.")
    String introduction
) {

}

