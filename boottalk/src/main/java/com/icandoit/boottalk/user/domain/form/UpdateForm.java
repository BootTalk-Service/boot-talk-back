package com.icandoit.boottalk.user.domain.form;

import lombok.Builder;

@Builder
public record UpdateForm(
    String profileImage,
    String desiredCareer
) {
}
