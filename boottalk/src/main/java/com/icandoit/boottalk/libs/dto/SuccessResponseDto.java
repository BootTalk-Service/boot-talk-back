package com.icandoit.boottalk.libs.dto;


/**
 * @param message 응답 메시지
 * @param data    선택적으로 포함할 데이터
 */
public record SuccessResponseDto<T>(String message, T data) {

    /**
     * 성공 응답 생성 - 데이터와 메시지 포함
     */
    public static <T> SuccessResponseDto<T> of(String message, T data) {
        return new SuccessResponseDto<>(message, data);
    }

    /**
     * 성공 응답 생성 - 메시지만 포함
     */
    public static <T> SuccessResponseDto<T> of(String message) {
        return new SuccessResponseDto<>(message, null);
    }
}