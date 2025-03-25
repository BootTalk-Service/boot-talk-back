package com.icandoit.boottalk.user.service;

import com.icandoit.boottalk.user.domain.dto.UserDto;
import com.icandoit.boottalk.user.domain.form.SignUpForm;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import java.math.BigInteger;

public interface ManageService {

  /**
   * 회원 조회
   * @param userId
   * @return
   */
  UserDto getUser(BigInteger userId);

  /**
   * 회원정보 수정
   * @param form
   * @return
   */
  UserDto updateUser(BigInteger userId ,UpdateForm form);

  /**
   * 회원탈퇴
   * @param userId
   */
  void deleteUser(BigInteger userId);
}
