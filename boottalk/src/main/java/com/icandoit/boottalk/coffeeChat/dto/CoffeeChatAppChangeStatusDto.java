package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;

import jakarta.validation.constraints.NotNull;

public record CoffeeChatAppChangeStatusDto(
    @NotNull(message = "커피챗 신청 상태는 필수입니다.")
    StatusType changeStatus
) {

}

