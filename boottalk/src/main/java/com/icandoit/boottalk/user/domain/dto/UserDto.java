package com.icandoit.boottalk.user.domain.dto;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.user.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private String name;
  private String email;
  private String profileImage;
  private BootcampCategoryType desiredCareer;

  public static UserDto from(User user) {
    return UserDto.builder()
        .name(user.getUserName())
        .email(user.getEmail())
        .profileImage(user.getProfileImage())
        .desiredCareer(user.getDesiredCareer())
        .build();
  }
}
