package com.icandoit.boottalk.coffeeChat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CoffeeChatApplicationUpdateDto(
    @NotNull(message = "커피챗 정보 ID는 필수입니다.")
    Long coffeeChatInfoId,

    @NotBlank(message = "신청 내용은 필수입니다.")
    @Size(max = 1000, message = "소개글은 최대 1000자까지 입력할 수 있습니다.")
    String content
) {

}

