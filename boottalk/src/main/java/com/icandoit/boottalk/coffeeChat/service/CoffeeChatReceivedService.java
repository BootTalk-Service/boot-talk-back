package com.icandoit.boottalk.coffeeChat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatAppChangeStatusDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatAppStatusResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.stomp_chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatReceivedService {

    private final CoffeeChatApplicationRepository coffeeChatAppRepository;

    private final CoffeeChatCommonService coffeeChatCommonService;
    private final CreatePointHistoryService createPointHistoryService;
    private final SseEmitterService sseEmitterService;
    private final ChatRoomService chatRoomService;

    private static final int MENTORING_BAN_DAYS = 30;

    // 나에게 신청한 커피챗 신정 목록 조회
    public PagedResponseDto<CoffeeChatApplicationResponseDto> getReceivedCoffeeChatApplications(
        Long userId, Pageable pageable) {

        // 커피챗 정보 조회
        CoffeeChatInfo coffeeChatInfo = coffeeChatCommonService.getOptionalCoffeeChatInfoByUserId(userId).orElse(null);

        if (coffeeChatInfo == null) {
            Page<CoffeeChatApplicationResponseDto> emptyPage = Page.empty(pageable);
            return PagedResponseDto.from(emptyPage);
        }

        Page<CoffeeChatApplicationResponseDto> page =
            coffeeChatAppRepository.findByCoffeeChatInfo_CoffeeChatInfoId(coffeeChatInfo.getCoffeeChatInfoId(), pageable)
                .map(CoffeeChatApplicationResponseDto::from);

        return PagedResponseDto.from(page);
    }

    @Transactional
    public CoffeeChatAppStatusResponseDto changeCoffeeChatAppStatus(
        Long userId, Long coffeeChatAppId, CoffeeChatAppChangeStatusDto request) {

        CoffeeChatApplication coffeeChatApp = coffeeChatCommonService.getCoffeeChatApplication(coffeeChatAppId);

        Long mentorId = coffeeChatApp.getCoffeeChatInfo().getMentor().getUserId();
        Long menteeId = coffeeChatApp.getMentee().getUserId();

        coffeeChatCommonService.validateCoffeeChatOwner(mentorId, userId);

        StatusType changeStatus = request.changeStatus();
        StatusType currentStatus = coffeeChatApp.getStatus();

        // 승인된 커피챗을 1일 전 멘토가 취소할 경우, 멘토 활동 30일 금지 패널티 부여
        if (changeStatus.isCanceled() && currentStatus.isApproved() &&
            !coffeeChatApp.isNDaysOrMoreUntilStart(1)) {
            coffeeChatApp.getCoffeeChatInfo().applyMentoringBan(MENTORING_BAN_DAYS);
        }

        // 멘토가 커피챗 거절/취소 시, 멘티에게 포인트 환불
        if (changeStatus.isRejected() || changeStatus.isCanceled()) {
            createPointHistoryService.createPointHistory(
                EventType.COFFEE_CHAT_CANCEL_REFUND,
                menteeId,
                coffeeChatApp.getUsedPoint()
            );
        }

        if (changeStatus.isApproved()) {
            chatRoomService.createChatRoom(coffeeChatApp);
        }

        coffeeChatApp.setStatus(changeStatus); // 상태 변경

        // 멘토에게 커피챗 수락/거절/취소 알림 전송
        sseEmitterService.sendToClient(
            menteeId,
            NotificationRequestDto.ofType(changeStatus.toNotificationType())
        );

        return CoffeeChatAppStatusResponseDto.from(coffeeChatApp);

    }



}
