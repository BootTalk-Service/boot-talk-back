package com.icandoit.boottalk.user.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.user.domain.dto.UserDto;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManageService {

	private final UserRepository userRepository;

	@Transactional
	public UserDto getUser(Long userId) {

		return UserDto.from(getUserInfo(userId));
	}

	@Transactional
	public UserDto updateUser(Long userId, UpdateForm form) {

		return UserDto.from(getUserInfo(userId).updateOf(form));
	}

	@Transactional
	public void deleteUser(Long userId) {

		User user = getUserInfo(userId);
		user.setDeletedAt(Timestamp.from(Instant.now()));
	}


	//사용자 정보 불러오기
	private User getUserInfo(Long userId) {

		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));
	}
}
