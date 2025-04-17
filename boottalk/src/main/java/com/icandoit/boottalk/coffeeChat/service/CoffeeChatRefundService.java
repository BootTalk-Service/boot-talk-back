package com.icandoit.boottalk.coffeeChat.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.event.NotificationEvent;
import com.icandoit.boottalk.notification.type.NotificationType;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeeChatRefundService {

    private final CoffeeChatApplicationRepository coffeeChatAppRepository;
    private final CreatePointHistoryService createPointHistoryService;
    private final ApplicationEventPublisher eventPublisher;

    // 멘토의 무응답으로 커피챗 시작 시간이 지난 신청 내역에 대한 환불 처리
    @Transactional
    public void refundExpiredPendingChats() {
        log.info("커피챗 자동 환불 시작");

        List<CoffeeChatApplication> coffeeChatApplications =
            coffeeChatAppRepository.findAllExpiredPendingApplications();

        // 해당 커피챗 신청에 대한 환불 처리
        for (CoffeeChatApplication coffeeChatApp : coffeeChatApplications) {
            try {
                Long menteeId = coffeeChatApp.getMentee().getUserId();
                int refundPoint = coffeeChatApp.getUsedPoint();

                createPointHistoryService.createPointHistory(
                    EventType.COFFEE_CHAT_NO_RESPONSE_REFUND,
                    menteeId,
                    refundPoint
                );

                coffeeChatApp.setStatus(StatusType.AUTO_CANCELED);
                coffeeChatAppRepository.save(coffeeChatApp);

                eventPublisher.publishEvent(new NotificationEvent(
                    menteeId,
                    NotificationRequestDto.ofType(NotificationType.COFFEE_CHAT_AUTO_REFUND)
                ));

                log.info("커피챗 자동 환불. menteeId: {}, coffeeChatAppId: {}, refundPoint: {}",
                    menteeId, coffeeChatApp.getCoffeeChatInfo().getCoffeeChatInfoId(), refundPoint);

            } catch (Exception e) {
                    log.error("커피챗 자동 환불 처리 중 오류 발생 ", e);
            }
        }
        log.info("커피챗 자동 환불 완료");

    }

}
