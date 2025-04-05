package com.icandoit.boottalk.user.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddUserInfoService {

  private final UserRepository userRepository;
  private final CreatePointHistoryService createPointHistoryService;

  @Transactional
  public void addUserInfo(UpdateForm form, long userId) {

    User user = userRepository.findById(userId).orElseThrow(
        () -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // 신규회원, 탈퇴한지 6개월이 지난 회원인 경우에만
    // 포인트 적립
    canReceivePoint(user);

    user.updateOf(form);
  }

  private void canReceivePoint(User user) {
    if (userRepository.existsByResourceUserIdAndDeletedWithinSixMonths(user.getResourceUserId(),
        Timestamp.valueOf(LocalDateTime.now().minusMonths(6)))) {
      return;
    }
    createPointHistoryService.createPointHistory(EventType.SIGN_UP, user.getUserId(), 5);
  }

}
