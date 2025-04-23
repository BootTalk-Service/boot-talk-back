package com.icandoit.boottalk.libs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    INVALID_DAY_FORMAT(400, "유효하지 않은 요일 형식입니다."),
    INVALID_TIME_FORMAT(400, "유효하지 않은 시간 형식입니다."),
    INSUFFICIENT_POINT(400, "잔여 포인트가 부족합니다."),
    EXCEEDS_MAX_LENGTH(400, "최대 길이를 초과했습니다."),
    COFFEE_CHAT_STATUS_NOT_PENDING(400, "대기 중 상태가 아니므로 상태를 변경할 수 없습니다."),
    CHAT_ROOM_NOT_ACTIVE(400, "채팅방이 비활성화되어 입장할 수 없습니다."),
    COFFEE_CHAT_CANNOT_CANCEL(400, "커피챗 신청 취소는 대기 또는 수락 중일 때만 가능합니다."),
    COFFEE_CHAT_DELETE_BANNED(400, "멘토링 활동 금지 기간에는 커피챗 정보를 삭제할 수 없습니다."),
    INVALID_CATEGORY_NAME(400, "잘못된 카테고리 이름입니다."),
    FILE_IS_EMPTY(400, "파일이 비어 있습니다."),
    INVALID_IMAGE_TYPE(400, "이미지 파일만 업로드할 수 있습니다."),
    INVALID_FILE_NAME(400, "잘못된 파일명입니다."),


    /* 401 UNAUTHORIZED */
    INVALID_TOKEN(401, "유효한 토큰이 아닙니다."),
    AUTHENTICATION_FAILED(401, "사용자 인증에 실패했습니다."),

    /* 403 FORBIDDEN */
    FORBIDDEN(403, "권한이 없습니다."),
    UNAUTHORIZED_USER(403, "사용자 인증 정보가 없습니다."),
    INVALID_AUTHENTICATION_TYPE(403,"oAuth2 인증 방식이 아닙니다." ),
    NOT_REVIEW_OWNER(403, "사용자가 작성한 리뷰가 아닙니다."),
    NOT_COFFEE_CHAT_APPLICATION_OWNER(403, "사용자가 작성한 커피챗 신청 내역이 아닙니다."),
    NOT_COFFEE_CHAT_INFO_OWNER(403, "사용자가 작성한 커피챗 정보가 아닙니다."),
    CHAT_ROOM_FORBIDDEN(403, "해당 채팅방에 접근할 수 있는 권한이 없습니다."),
    WRITE_REVIEW_FORBIDDEN(403, "리뷰 작성할 권한이 없습니다."),

    /* 404 NOT_FOUND */
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
    BOOTCAMP_NOT_FOUND(404, "부트캠프를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다."),
    COFFEE_CHAT_NOT_FOUND(404, "커피챗을 찾을 수 없습니다."),
    USER_COFFEE_CHAT_NOT_FOUND(404, "유저의 해당하는 커피챗을 찾을 수 없습니다."),
    COFFEE_CHAT_APPLICATION_NOT_FOUND(404, "커피챗 신청 내역을 찾을 수 없습니다."),
    COURSE_NOT_FOUND(404, "코스를 찾을 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(404, "채팅방을 찾을 수 없습니다."),
    TRAINING_CENTER_NOT_FOUND(404, "요청한 훈련 기관을 찾을 수 없습니다."),
    API_DATA_IS_EMPTY(404, "API 데이터가 비어 있습니다."),
    CHAT_ROOM_STATUS_NOT_FOUND(404, "채팅방 상태를 찾을 수 없습니다."),

    /* 409 CONFLICT */
    DUPLICATE_REVIEW(409, "해당 부트캠프에 이미 리뷰를 작성하였습니다."),
    DUPLICATE_CERTIFICATION_EXIST(409, "동일한 인증이 이미 존재합니다."),
    NOT_PENDING_STATUS(409, "커피챗 신청이 '대기 중' 상태일 때만 수정할 수 있습니다."),
    COFFEE_CHAT_ALREADY_EXISTS(409, "이미 생성된 커피챗이 있습니다."),
    COFFEE_CHAT_APPLICATION_ALREADY_EXISTS(409, "이미 해당 커피챗에 신청되었습니다."),
    COFFEE_CHAT_APPLICATION_TIME_ALREADY_EXISTS(400, "이미 신청된 시간입니다."),
    CHAT_ROOM_NOT_STARTED(409, "채팅 시간이 아직 시작되지 않았습니다."),

    /* 410 GONE */
    CHAT_ROOM_ENDED(410, "채팅 시간이 종료되어 메시지를 보낼 수 없습니다."),
    CHAT_ROOM_EXPIRED(410, "채팅방의 유효 시간이 만료되었습니다."),
    MESSAGE_EXPIRED(410, "메시지가 만료되었습니다."),

    /* SERVICE_UNAVAILABLE */
    SSE_CONNECTION_FAILED(503, "SSE 연결 오류가 발생하였습니다."),

    /* 500 INTERNAL_SERVER_ERROR */
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
    TOKEN_PARSING_ERROR(500, "토큰 파싱 과정에서 오류가 발생했습니다."),
    DATA_FETCH_ERROR(500, "데이터 요청 중 오류가 발생했습니다."),
    DATA_PARSING_ERROR(500, "API 응답 데이터 파싱에 실패했습니다."),
    FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다."),
    SCHEDULER_START_JOB_ERROR(500, "시작 스케줄러 작업 중 오류가 발생했습니다."),
    SCHEDULER_END_JOB_ERROR(500, "종료 스케쥴러 작업 중 오류가 발생했습니다.");

    private final Integer httpStatus;
    private final String message;
}