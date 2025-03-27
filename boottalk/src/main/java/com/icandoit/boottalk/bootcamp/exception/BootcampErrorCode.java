package com.icandoit.boottalk.bootcamp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BootcampErrorCode {

	/* 400 BAD_REQUEST */
	INVALID_CATEGORY_NAME(400, "잘못된 카테고리 이름입니다."),
	INVALID_BOOTCAMP_ID(400, "유효하지 않은 부트캠프 ID 입니다."),

	/* 404 NOT_FOUND */
	BOOTCAMP_NOT_FOUND(404, "요청한 부트캠프를 찾을 수 없습니다."),
	TRAINING_CENTER_NOT_FOUND(404, "요청한 훈련 기관을 찾을 수 없습니다."),
	API_DATA_IS_EMPTY(404, "API 데이터가 비어 있습니다."),
	DATA_FETCH_ERROR(404, "데이터 요청 중 오류가 발생했습니다.");

	private final Integer httpStatus;
	private final String message;
}
