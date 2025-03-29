package com.icandoit.boottalk.libs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST */


    /* 403 FORBIDDEN */
    FORBIDDEN(403, "권한이 없습니다."),
    NOT_REVIEW_OWNER(403, "사용자가 작성한 리뷰가 아닙니다."),

    /* 404 NOT_FOUND */
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(404,"유저를 찾을 수 없습니다."),
    BOOTCAMP_NOT_FOUND(404,"부트캠프를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(404,"리뷰를 찾을 수 없습니다."),

    /* 409 CONFLICT */
    DUPLICATE_REVIEW(409,"해당 부트캠프에 이미 리뷰를 작성하였습니다."),

    /* 500 INTERNAL_SERVER_ERROR */
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다.");

    private final Integer httpStatus;
    private final String message;
}