package com.icandoit.boottalk.coffeeChat.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatScheduleService;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.user.domain.entity.User;

@ExtendWith(MockitoExtension.class)
class CoffeeChatScheduleServiceTest {

    @InjectMocks
    CoffeeChatScheduleService coffeeChatScheduleService;

    @Mock
    private CoffeeChatApplicationRepository coffeeChatAppRepository;

    @Mock
    private CreatePointHistoryService createPointHistoryService;

    private CoffeeChatInfo mockChatInfo;
    private User mentee1;
    private User mentee2;
    private CoffeeChatApplication mockChatApp1;
    private CoffeeChatApplication mockChatApp2;
    private int coffeeChatPoint1;
    private int coffeeChatPoint2;

    @BeforeEach
    void setUp() {
        mockChatInfo = mock(CoffeeChatInfo.class);
        when(mockChatInfo.getCoffeeChatInfoId()).thenReturn(11L);

        mentee1 = mock(User.class);
        when(mentee1.getUserId()).thenReturn(1L);
        mentee2 = mock(User.class);
        when(mentee2.getUserId()).thenReturn(2L);

        coffeeChatPoint1 = 2;
        coffeeChatPoint2 = 3;

        mockChatApp1 = getMockApplication(mentee1, mockChatInfo, coffeeChatPoint1);
        mockChatApp2 = getMockApplication(mentee2, mockChatInfo, coffeeChatPoint2);

        // 만료된 커피챗 신청 목록을 반환 설정
        when(coffeeChatAppRepository.findAllExpiredPendingApplications())
            .thenReturn(List.of(mockChatApp1, mockChatApp2));

    }

    @Test
    @DisplayName("대기 중이면서 시작 시간이 지난 커피챗 신청이 2건인 경우 모두 환불됨")
    void refundExpiredPendingChats_shouldRefundSuccessfully() {

        // given

        // when
        // 환불 처리 실행
        coffeeChatScheduleService.refundExpiredPendingChats();

        // then
        // 포인트 히스토리가 잘 생성되었는지 확인
        verify(createPointHistoryService).createPointHistory(EventType.COFFEE_CHAT_NO_RESPONSE_REFUND, mentee1.getUserId(), coffeeChatPoint1);
        verify(createPointHistoryService).createPointHistory(EventType.COFFEE_CHAT_NO_RESPONSE_REFUND, mentee2.getUserId(), coffeeChatPoint2);


        // 커피챗 상태가 자동 취소로 변경 되었는지 확인
        assertEquals(StatusType.AUTO_CANCELED, mockChatApp1.getStatus());
        assertEquals(StatusType.AUTO_CANCELED, mockChatApp2.getStatus());

    }


    @Test
    @DisplayName("환불 처리 중 하나에서 예외가 발생해도 나머지는 정상적으로 환불됨")
    void refundExpiredPendingChats_shouldHandleExceptionGracefully() {
        // given

        // mentee1의 신청 내역은 환불 처리 중 예외 발생 적용
        when(createPointHistoryService.createPointHistory(EventType.COFFEE_CHAT_NO_RESPONSE_REFUND, mentee1.getUserId(), coffeeChatPoint1))
            .thenThrow(new RuntimeException("예외 발생"));

        // when
        // 환불 처리 실행
        coffeeChatScheduleService.refundExpiredPendingChats();

        // then
        // 포인트 히스토리가 잘 생성되었는지 확인
        verify(createPointHistoryService).createPointHistory(EventType.COFFEE_CHAT_NO_RESPONSE_REFUND, mentee2.getUserId(), coffeeChatPoint2);

        // 먼저 환불 처리 진행한 mentee1 에서 예외 처리가 발생하더라도 다음 환불 처리 대상 mentee2의 환불 처리가 정상적으로 처리됨을 확인
        assertEquals(StatusType.AUTO_CANCELED, mockChatApp2.getStatus());

    }

    private CoffeeChatApplication getMockApplication(User mentee, CoffeeChatInfo coffeeChatInfo, int coffeeChatPoint) {
        LocalDateTime coffeeChatSTartTime = LocalDateTime.now().minusDays(1);
        return CoffeeChatApplication.builder()
            .mentee(mentee)
            .coffeeChatInfo(coffeeChatInfo)
            .usedPoint(coffeeChatPoint)
            .content("커피챗 신청합니다.")
            .coffeeChatStartTime(coffeeChatSTartTime)
            .coffeeChatEndTime(coffeeChatSTartTime.plusMinutes(30))
            .status(StatusType.PENDING) // 신청 상태를 대기중으로 설정
            .build();

    }

}
