package com.icandoit.boottalk.user.service.impl;

import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.form.SignUpForm;
import com.icandoit.boottalk.user.domain.repository.UserRepository;
import com.icandoit.boottalk.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public void signUp(SignUpForm form) {

    User user = userRepository.findById(form.getUserId()).orElse(null);

    // 이미 회원가입된 유저인 경우에는 예외처리
    // 회원가입되었던 유저중에서 회원탈퇴한 회원이 다시 회원가입 시도하는 경우,
    // 기존 회원정보 삭제 후 다시 새롭게 회원정보 입력
    if (user != null) {
      if (user.getDeletedAt() == null) {
        throw new RuntimeException("이미 가입한 회원입니다.");
      }
      userRepository.delete(user);
    }

    userRepository.save(User.from(form));
  }


}
