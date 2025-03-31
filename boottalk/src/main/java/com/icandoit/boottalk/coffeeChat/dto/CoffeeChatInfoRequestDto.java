package com.icandoit.boottalk.coffeeChat.dto;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;

public record CoffeeChatInfoRequestDto(
    @NotBlank(message = "userType은 필수 입력값입니다.")
    String userType,

    @NotBlank(message = "jobType은 필수 입력값입니다.")
    String jobType,

    @NotBlank(message = "소개글은 필수 입력값입니다.")
    String introduction
) {
    private static final int MAX_INTRODUCTION_LENGTH = 1000;
    public void validate() {
        if (introduction != null && introduction.length() > MAX_INTRODUCTION_LENGTH) {
            throw new CustomException(ErrorCode.EXCEEDS_MAX_LENGTH);
        }
    }
}

