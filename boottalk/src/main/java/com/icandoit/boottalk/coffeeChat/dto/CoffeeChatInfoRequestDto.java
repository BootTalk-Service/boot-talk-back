package com.icandoit.boottalk.coffeeChat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CoffeeChatInfoRequestDto(
    @NotBlank(message = "userType은 필수 입력값입니다.")
    String userType,

    @NotBlank(message = "jobType은 필수 입력값입니다.")
    String jobType,

    @NotBlank(message = "소개글은 필수 입력값입니다.")
    @Size(max = 1000, message = "소개글은 최대 1000자까지 입력할 수 있습니다.")
    String introduction
) {

}

