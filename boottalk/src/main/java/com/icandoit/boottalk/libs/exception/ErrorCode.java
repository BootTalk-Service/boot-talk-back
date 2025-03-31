package com.icandoit.boottalk.libs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    INSUFFICIENT_POINT(400, "잔여 포인트가 부족합니다."),

    /* 401 UNAUTHORIZED */

    EXCEEDS_MAX_LENGTH(400,"최대 길이를 초과했습니다." ),
    /* 403 FORBIDDEN */
    FORBIDDEN(403, "권한이 없습니다."),

    /* 404 NOT_FOUND */
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(404,"유저를 찾을 수 없습니다."),
    USER_COFFEE_CHAT_NOT_FOUND(404,"유저의 해당하는 커피챗을 찾을 수 없습니다."),

    /* 500 INTERNAL_SERVER_ERROR */
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다.");


    private final Integer httpStatus;
    private final String message;
}