package com.icandoit.boottalk.user.controller;

import com.icandoit.boottalk.common.dto.BaseResponse;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.service.ManageService;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/my")
public class ManageController {

  private final ManageService manageService;

  @GetMapping
  public ResponseEntity<BaseResponse> getUser(@RequestParam BigInteger userId) {

    return ResponseEntity.ok(new BaseResponse("회원정보가 조회되었습니다.", manageService.getUser(userId)));
  }

  @PutMapping
  public ResponseEntity<BaseResponse> updateUser(@RequestParam BigInteger userId, @RequestBody UpdateForm form) {

    return ResponseEntity.ok(new BaseResponse("회원정보가 수정되었습니다.", manageService.updateUser(userId, form)));
  }

  @DeleteMapping
  public ResponseEntity<BaseResponse> deleteUser(@RequestParam BigInteger userId) {

    manageService.deleteUser(userId);

    return ResponseEntity.ok(new BaseResponse("회원 탈퇴되었습니다.", null));
  }



}
