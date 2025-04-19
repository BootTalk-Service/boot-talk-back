package com.icandoit.boottalk.coffeeChat.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationUpdateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoApprovedDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.event.NotificationEvent;
import com.icandoit.boottalk.notification.type.NotificationType;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.user.domain.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeeChatApplicationService {

    private final CoffeeChatApplicationRepository coffeeChatAppRepository;

    private final CoffeeChatCommonService coffeeChatCommonService;
    private final CreatePointHistoryService createPointHistoryService;
	private final ApplicationEventPublisher eventPublisher;

    // 멘토 타입에 따른 포인트 차감액
    private final int GENERAL_COFFEE_CHAT_POINT = 1;
    private final int GRADUATE_COFFEE_CHAT_POINT = 2;
    private final int PROFESSIONAL_COFFEE_CHAT_POINT = 3;

    @Transactional
    public CoffeeChatApplicationResponseDto createCoffeeChatApp(Long userId, CoffeeChatApplicationCreateDto request) {

        Long coffeChatInfoId = request.coffeeChatInfoId();

        // 해당 커피챗에 대해 대기 또는 수락 상태의 신청이 이미 존재하면 예외 처리 (중복 신청 방지)
        if (coffeeChatAppRepository.existsLatestPendingOrApprovedApplication(userId, coffeChatInfoId)) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_ALREADY_EXISTS);
        }

        log.debug("커피챗 신청 Lock 시작: userId: {}", userId);

        // 동일 시간대에 다른 사용자의 커피챗 신청이 존재하면 예외 처리 (동시성 제어용 Lock 설정)
        if (coffeeChatAppRepository.isTimeSlotAlreadyTaken(coffeChatInfoId, request.coffeeChatStartTime())) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_TIME_ALREADY_EXISTS);
        }

        log.debug("커피챗 신청 Lock 해제: userId: {}", userId);

        User user = coffeeChatCommonService.getUser(userId);
        CoffeeChatInfo coffeeChatInfo = coffeeChatCommonService.getCoffeeChatInfo(coffeChatInfoId);

        MentorType mentorType = coffeeChatInfo.getMentorType();
        int deductionPoint = getPointCostByMentorType (mentorType); // 멘토 타입에 따른 커피챗 포인트

        createPointHistoryService.createPointHistory(EventType.COFFEE_CHAT_APPLY, userId, deductionPoint);

        CoffeeChatApplication coffeeChatApp = CoffeeChatApplication.of(user, coffeeChatInfo, deductionPoint, request);
        coffeeChatAppRepository.save(coffeeChatApp);

        // 멘토에게 커피챗 신청 알림 전송
        eventPublisher.publishEvent(new NotificationEvent(
            coffeeChatInfo.getMentor().getUserId(),
            NotificationRequestDto.ofType(NotificationType.COFFEE_CHAT_REQUEST_RECEIVED)
        ));

        return CoffeeChatApplicationResponseDto.from(coffeeChatApp);

    }

	private int getPointCostByMentorType (MentorType mentorType) {
		return switch(mentorType){
            case  GENERAL -> GENERAL_COFFEE_CHAT_POINT;
            case  GRADUATE -> GRADUATE_COFFEE_CHAT_POINT;
			case  PROFESSIONAL -> PROFESSIONAL_COFFEE_CHAT_POINT;
		};
	}

	@Transactional(readOnly = true)
    public PagedResponseDto<CoffeeChatApplicationResponseDto> getMyCoffeeChatApps(Long userId, Pageable pageable) {
        Page<CoffeeChatApplicationResponseDto> page = coffeeChatAppRepository.findByMentee_UserId(userId, pageable)
            .map(CoffeeChatApplicationResponseDto::from);

        return PagedResponseDto.from(page);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<CoffeeChatInfoApprovedDto> getApprovedCoffeeChats(Long userId, Pageable pageable) {
        Page<CoffeeChatInfoApprovedDto> page = coffeeChatAppRepository.findApprovedChatsByUserId(userId, pageable)
            .map(CoffeeChatInfoApprovedDto::from);

        return PagedResponseDto.from(page);
    }

    public CoffeeChatApplicationResponseDto getCoffeeChatAppInfo(Long coffeeChatAppId) {
        return CoffeeChatApplicationResponseDto.from(
            coffeeChatCommonService.getCoffeeChatApplication(coffeeChatAppId));
    }

    @Transactional
    public CoffeeChatApplicationResponseDto updateCoffeeChatApp(
        Long userId, Long coffeeChatAppId, CoffeeChatApplicationUpdateDto request) {
        CoffeeChatApplication coffeeChatApp = coffeeChatCommonService.getCoffeeChatApplication(coffeeChatAppId);

        coffeeChatCommonService.validateCoffeeChatApplicant(userId, coffeeChatApp.getMentee().getUserId());
        coffeeChatCommonService.validateCoffeeChatInfo(coffeeChatApp.getCoffeeChatInfo().getCoffeeChatInfoId());

        // 상태가 대기 중일 때만 content 수정 가능
        if (coffeeChatApp.getStatus() != StatusType.PENDING) {
            throw new CustomException(ErrorCode.NOT_PENDING_STATUS);
        }

        coffeeChatApp.setContent(request.content());

        return CoffeeChatApplicationResponseDto.from(coffeeChatApp);
    }

    @Transactional
    public void cancelCoffeeChatApp(Long userId, Long coffeeChatAppId) {
        CoffeeChatApplication coffeeChatApp = coffeeChatCommonService.getCoffeeChatApplication(coffeeChatAppId);

        coffeeChatCommonService.validateCoffeeChatApplicant(userId, coffeeChatApp.getMentee().getUserId());
        coffeeChatCommonService.validateCoffeeChatInfo(coffeeChatApp.getCoffeeChatInfo().getCoffeeChatInfoId());

        StatusType currentStatus = coffeeChatApp.getStatus();

        // 커피챗 취소는 상태가 대기 또는 수락 중일 때만 가능
        if (!(currentStatus.isPending() || currentStatus.isApproved())) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_CANNOT_CANCEL);
        }

        // 커피챗 취소시 환불 처리
        // 상태가 대기이거나, 상태가 승인이고 커피챗 시작 시간 2일 전까지는 환불 처리
        if (currentStatus.isPending() ||
            currentStatus.isApproved() && coffeeChatApp.isNDaysOrMoreUntilStart(2)
        ) {
            createPointHistoryService.createPointHistory(EventType.COFFEE_CHAT_CANCEL_REFUND, userId, coffeeChatApp.getUsedPoint());
        }

        coffeeChatApp.setStatus(StatusType.CANCELED);

        // 멘토에게 커피챗 취소 알린 전송
        eventPublisher.publishEvent(new NotificationEvent(
            userId,
            NotificationRequestDto.ofType(NotificationType.COFFEE_CHAT_REQUEST_CANCELLED_FROM_MENTEE)
        ));

    }


}
