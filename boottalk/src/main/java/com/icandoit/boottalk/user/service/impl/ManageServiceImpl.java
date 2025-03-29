package com.icandoit.boottalk.user.service.impl;

import com.icandoit.boottalk.user.domain.dto.UserDto;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.domain.repository.UserRepository;
import com.icandoit.boottalk.user.service.ManageService;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManageServiceImpl implements ManageService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDto getUser(Long userId) {

		User user = validateUser(userId);

		return UserDto.from(user);
	}

	@Override
	@Transactional
	public UserDto updateUser(Long userId, UpdateForm form) {

		User user = validateUser(userId);

		return UserDto.from(userRepository.save(user.updateFrom(form)));
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {

		User user = validateUser(userId);

		user.setDeletedAt(Timestamp.from(Instant.now()));
		userRepository.save(user);
	}


	//유효한 회원인지 확인
	private User validateUser(Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("조회된 회원 정보가 없습니다."));

		if (user.getDeletedAt() != null) {
			throw new RuntimeException("탈퇴한 회원입니다.");
		}

		return user;
	}
}
