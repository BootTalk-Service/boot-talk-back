package com.icandoit.boottalk.user.domain.form;

import com.icandoit.boottalk.user.domain.type.DesiredCareer;
import java.math.BigInteger;
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
public class UpdateForm {
  private String name;
  private String email;
  private String profileImage;
  private DesiredCareer desiredCareer;
}
