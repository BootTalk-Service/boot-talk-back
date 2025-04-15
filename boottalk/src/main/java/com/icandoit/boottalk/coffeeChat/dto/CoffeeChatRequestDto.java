package com.icandoit.boottalk.coffeeChat.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CoffeeChatRequestDto(
    @Valid CoffeeChatInfoRequestDto info,

    @NotNull(message = "멘토링 가능 시간은 필수 항목입니다.")
    @Size(min = 1, message = "멘토링 가능 시간을 1개 이상 선택해주세요.")
    Map<String, List<String>> time
) {

}