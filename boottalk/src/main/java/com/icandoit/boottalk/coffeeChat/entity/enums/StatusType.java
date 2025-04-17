package com.icandoit.boottalk.coffeeChat.entity.enums;

import com.icandoit.boottalk.notification.type.NotificationType;

public enum StatusType {
    PENDING,    // 대기 - 멘토가 아직 수락/거절하지 않은 상태
    CANCELED,   // 취소 - 멘티가 신청을 취소한 상태
    AUTO_CANCELED,   // 자동 취소 - 멘토의 무응답으로 자동 취소된 상태
    REJECTED,   // 거절 - 멘토가 신청을 거절한 상태
    APPROVED,   // 수락 - 멘토가 신청을 수락한 상태
    COMPLETED  // 완료 - 커피챗 일정이 종료된 상태
    ;

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isApproved() {
        return this == APPROVED;
    }

    public NotificationType toNotificationType() {
        return switch (this) {
            case APPROVED -> NotificationType.COFFEE_CHAT_REQUEST_APPROVED;
            case REJECTED -> NotificationType.COFFEE_CHAT_REQUEST_REJECTED;
            case CANCELED -> NotificationType.COFFEE_CHAT_REQUEST_CANCELLED_FROM_MENTOR;
            default -> throw new IllegalStateException("Unexpected StatusType: " + this);
        };
    }

}
