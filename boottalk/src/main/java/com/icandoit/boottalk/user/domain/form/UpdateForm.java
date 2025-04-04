package com.icandoit.boottalk.user.domain.form;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
public record UpdateForm(
    String profileImage,
    BootcampCategoryType desiredCareer
) {
}
