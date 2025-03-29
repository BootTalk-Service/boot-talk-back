package com.icandoit.boottalk.user.service;

import com.icandoit.boottalk.user.domain.form.SignUpForm;

public interface SignUpService {
  /**
   * 회원 가입
   * @param form
   */
  void signUp(SignUpForm form);


}
