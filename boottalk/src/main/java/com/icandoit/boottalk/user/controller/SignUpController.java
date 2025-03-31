package com.icandoit.boottalk.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.user.domain.form.SignUpForm;
import com.icandoit.boottalk.user.service.SignUpService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/signup")
public class SignUpController {

  private final SignUpService signUpService;

  @PostMapping
  public ResponseEntity<String> signUp(@RequestBody SignUpForm form) {

    signUpService.signUp(form);

    return ResponseEntity.ok("회원가입이 완료되었습니다");
  }

}
