package com.icandoit.boottalk.user.domain.dto;

import lombok.Builder;

@Builder
public record UserUpdateDto(
    String profileImage,
    String desiredCareer
) {
}
