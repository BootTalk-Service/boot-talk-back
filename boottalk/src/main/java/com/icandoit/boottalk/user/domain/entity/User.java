package com.icandoit.boottalk.user.domain.entity;

import com.icandoit.boottalk.common.entity.BaseEntity;
import com.icandoit.boottalk.user.domain.form.SignUpForm;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.domain.type.DesiredCareer;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.math.BigInteger;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {

  @Id
  private BigInteger userId;

  private String name;
  private String email;
  private String profileImage;

  @Enumerated(EnumType.STRING)
  private DesiredCareer desiredCareer;

  private Timestamp deletedAt;

  public static User from(SignUpForm form) {
    return User.builder()
        .userId(form.getUserId())
        .name(form.getName())
        .email(form.getEmail())
        .profileImage(form.getProfileImage())
        .desiredCareer(form.getDesiredCareer())
        .deletedAt(null)
        .build();
  }

  public User updateFrom(UpdateForm form) {
    this.name = form.getName();
    this.email = form.getEmail();
    this.profileImage = form.getProfileImage();
    this.desiredCareer = form.getDesiredCareer();

    return this;
  }


}
