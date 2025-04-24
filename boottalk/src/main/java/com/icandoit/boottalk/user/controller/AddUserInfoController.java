package com.icandoit.boottalk.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.social_login.dto.CustomOAuth2User;
import com.icandoit.boottalk.user.domain.dto.UserUpdateDto;
import com.icandoit.boottalk.user.service.AddUserInfoService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/signup")
public class AddUserInfoController {

  private final AddUserInfoService addUserInfoService;

  @PostMapping
  public ResponseEntity<String> addUserInfo(@RequestBody UserUpdateDto form,
      @AuthenticationPrincipal CustomOAuth2User user) {

    addUserInfoService.addUserInfo(form, user.getServiceUserId());

    return ResponseEntity.ok("부트톡 회원이 되신 것을 축하드립니다.");
  }

}
