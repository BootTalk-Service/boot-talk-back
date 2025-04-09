package com.icandoit.boottalk.libs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    INSUFFICIENT_POINT(400, "잔여 포인트가 부족합니다."),
    EXCEEDS_MAX_LENGTH(400,"최대 길이를 초과했습니다." ),
    COFFEE_CHAT_STATUS_NOT_PENDING (400, "대기 중 상태가 아니므로 상태를 변경할 수 없습니다."),

    /* 401 UNAUTHORIZED */
    INVALID_TOKEN(401, "유효한 토큰이 아닙니다."),

    /* 403 FORBIDDEN */
    FORBIDDEN(403, "권한이 없습니다."),
    NOT_REVIEW_OWNER(403, "사용자가 작성한 리뷰가 아닙니다."),
    NOT_COFFEE_CHAT_APPLICATION_OWNER(403, "사용자가 작성한 커피챗 신청 내역이 아닙니다."),
    NOT_COFFEE_CHAT_INFO_OWNER(403, "사용자가 작성한 커피챗 정보가 아닙니다."),

    /* 404 NOT_FOUND */
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(404,"유저를 찾을 수 없습니다."),
    BOOTCAMP_NOT_FOUND(404,"부트캠프를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(404,"리뷰를 찾을 수 없습니다."),
    COFFEE_CHAT_NOT_FOUND(404,"커피챗을 찾을 수 없습니다."),
    USER_COFFEE_CHAT_NOT_FOUND(404,"유저의 해당하는 커피챗을 찾을 수 없습니다."),
    COFFEE_CHAT_APPLICATION_NOT_FOUND(404,"커피챗 신청 내역을 찾을 수 없습니다."),
    COURSE_NOT_FOUND(404, "코스를 찾을 수 없습니다."),

    /* 409 CONFLICT */
    DUPLICATE_REVIEW(409,"해당 부트캠프에 이미 리뷰를 작성하였습니다."),
    NOT_PENDING_STATUS(409, "커피챗 신청이 '대기 중' 상태일 때만 수정할 수 있습니다."),

    /* 409 Conflict */
    COFFEE_CHAT_ALREADY_EXISTS(409, "이미 생성된 커피챗이 있습니다."),

    /* 500 INTERNAL_SERVER_ERROR */
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
    TOKEN_PARSING_ERROR(500, "토큰 파싱 과정에서 오류가 발생했습니다.");

    private final Integer httpStatus;
    private final String message;
}