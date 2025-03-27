package com.icandoit.boottalk.user.domain.dto;

import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.type.DesiredCareer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private String name;
  private String email;
  private String profileImage;
  private DesiredCareer desiredCareer;

  public static UserDto from(User user) {
    return UserDto.builder()
        .name(user.getName())
        .email(user.getEmail())
        .profileImage(user.getProfileImage())
        .desiredCareer(user.getDesiredCareer())
        .build();
  }
}
