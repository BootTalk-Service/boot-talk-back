package com.icandoit.boottalk.user_test.form;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;


public record TestSignUpForm(
	String userName,
	String email,
	String resourceUserId,
	String profileImage,
	BootcampCategoryType desiredCareer
) {
}
