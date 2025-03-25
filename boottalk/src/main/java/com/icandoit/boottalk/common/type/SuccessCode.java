package com.icandoit.boottalk.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
	REVIEW_REGISTER_SUCCESS("리뷰 등록 완료"),
	GET_ALL_REVIEWS_SUCCESS("전체 리뷰 목록 조회 완료"),
	GET_USER_REVIEWS_SUCCESS("사용자가 작성한 리뷰 조회 성공"),
	REVIEW_UPDATE_SUCCESS("리뷰 수정 완료"),
	REVIEW_DELETE_SUCCESS("리뷰 삭제 성공")
	;

	private final String message;
}
