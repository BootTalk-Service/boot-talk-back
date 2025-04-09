package com.icandoit.boottalk.user.domain.form;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;

import lombok.Builder;

@Builder
public record UpdateForm(
    String profileImage,
    BootcampCategoryType desiredCareer
) {
}
