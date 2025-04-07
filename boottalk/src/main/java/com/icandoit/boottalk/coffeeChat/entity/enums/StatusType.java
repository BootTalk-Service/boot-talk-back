package com.icandoit.boottalk.coffeeChat.entity.enums;

public enum StatusType {
    PENDING,    // 대기 - 멘토가 아직 수락/거절하지 않은 상태
    CANCELED,   // 취소 - 멘티가 신청을 취소한 상태
    REJECTED,   // 거절 - 멘토가 신청을 거절한 상태
    APPROVED,   // 수락 - 멘토가 신청을 수락한 상태
    COMPLETED,  // 완료 - 커피챗 일정이 종료된 상태
}
