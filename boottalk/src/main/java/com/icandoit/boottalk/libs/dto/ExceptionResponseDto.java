package com.icandoit.boottalk.libs.dto;

import lombok.Getter;

/**
 * @param status  HTTP 상태 코드
 * @param message 예외 메시지
 * @param data    선택적으로 포함할 데이터
 */
@Getter
public record ExceptionResponseDto<T>(Integer status, String message, T data) {

    public static <T> ExceptionResponseDto<T> of(Integer status, String message, T data) {
        return new ExceptionResponseDto<>(status, message, data);
    }

    public static <T> ExceptionResponseDto<T> of(Integer status, String message) {
        return new ExceptionResponseDto<>(status, message, null);
    }
}