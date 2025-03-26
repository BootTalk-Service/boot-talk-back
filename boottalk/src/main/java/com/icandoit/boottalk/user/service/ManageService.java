package com.icandoit.boottalk.user.service;

import com.icandoit.boottalk.user.domain.dto.UserDto;
import com.icandoit.boottalk.user.domain.form.UpdateForm;

public interface ManageService {

	/**
	 * 회원 조회
	 *
	 * @param userId
	 * @return
	 */
	UserDto getUser(Long userId);

	/**
	 * 회원정보 수정
	 *
	 * @param form
	 * @return
	 */
	UserDto updateUser(Long userId, UpdateForm form);

	/**
	 * 회원탈퇴
	 *
	 * @param userId
	 */
	void deleteUser(Long userId);
}
