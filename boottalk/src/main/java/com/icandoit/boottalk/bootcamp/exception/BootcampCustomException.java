package com.icandoit.boottalk.bootcamp.exception;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;

import lombok.Getter;

@Getter
public class BootcampCustomException extends CustomException {

	private final BootcampErrorCode bootcampErrorCode;

	public BootcampCustomException(BootcampErrorCode bootcampErrorCode) {
		super(ErrorCode.INTERNAL_SERVER_ERROR);
		this.bootcampErrorCode = bootcampErrorCode;
	}

	@Override
	public String getMessage() {
		return bootcampErrorCode.getMessage();
	}
}
