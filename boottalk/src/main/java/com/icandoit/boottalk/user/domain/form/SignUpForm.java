package com.icandoit.boottalk.user.domain.form;

import org.antlr.v4.runtime.misc.NotNull;

import com.icandoit.boottalk.user.domain.type.DesiredCareer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpForm {

	private long userId;
	private String name;
	private String email;
	private String profileImage;
	private DesiredCareer desiredCareer;
}
