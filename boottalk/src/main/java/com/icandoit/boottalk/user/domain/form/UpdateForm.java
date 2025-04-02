package com.icandoit.boottalk.user.domain.form;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateForm {
  private String email;
  private String profileImage;
  private BootcampCategoryType desiredCareer;
}
