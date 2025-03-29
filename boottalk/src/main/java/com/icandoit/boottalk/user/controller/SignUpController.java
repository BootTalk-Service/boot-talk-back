package com.icandoit.boottalk.user.controller;

import com.icandoit.boottalk.common.dto.BaseResponse;
import com.icandoit.boottalk.user.domain.form.SignUpForm;
import com.icandoit.boottalk.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users/signup")
public class SignUpController {

  private final SignUpService signUpService;

  @PostMapping
  public ResponseEntity<BaseResponse> signUp(@RequestBody SignUpForm form) {

    signUpService.signUp(form);

    return ResponseEntity.ok(new BaseResponse("회원가입이 완료되었습니다", null));
  }

}
