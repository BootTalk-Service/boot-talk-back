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
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatReceivedService {

    private final CoffeeChatApplicationRepository coffeeChatAppRepository;

    private final CoffeeChatInfoService coffeeChatInfoService;
    private final CoffeeChatApplicationService coffeeChatAppService;
    private final CreatePointHistoryService createPointHistoryService;

    private static final int MENTORING_BAN_DAYS = 30;

    // 나에게 신청한 커피챗 신정 목록 조회
    public PagedResponseDto<CoffeeChatApplicationResponseDto> getReceivedCoffeeChatApplications(
        Long userId, Pageable pageable) {

        // 커피챗 정보 조회
        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoService.getCoffeeChatInfoByUserId(userId);

        Page<CoffeeChatApplicationResponseDto> page =
            coffeeChatAppRepository.findByCoffeeChatInfo_CoffeeChatInfoId(coffeeChatInfo.getCoffeeChatInfoId(), pageable)
                .map(CoffeeChatApplicationResponseDto::from);

        return PagedResponseDto.from(page);
    }

    @Transactional
    public CoffeeChatAppStatusResponseDto changeCoffeeChatAppStatus(
        Long userId, Long coffeeChatAppId, CoffeeChatAppChangeStatusDto request) {

        CoffeeChatApplication coffeeChatApp = coffeeChatAppService.getCoffeeChatApplication(coffeeChatAppId);

        Long mentorId = coffeeChatApp.getCoffeeChatInfo().getMentor().getUserId();

        validateCoffeeChatOwner(mentorId, userId);

        StatusType changeStatus = request.changeStatus();
        StatusType currentStatus = coffeeChatApp.getStatus();

        if (changeStatus == StatusType.CANCELED) {
            // 승인된 커피챗을 1일 전 멘토가 취소할 경우, 멘토 활동 30일 금지 패널티 부여
            if (currentStatus == StatusType.APPROVED && !coffeeChatApp.isNDaysOrMoreUntilStart(1)) {
                coffeeChatApp.getCoffeeChatInfo().applyMentoringBan(MENTORING_BAN_DAYS);
            }

            // 거절 또는 취소된 경우 -> 포인트 환불
            if (currentStatus == StatusType.REJECTED || currentStatus == StatusType.APPROVED) {
                createPointHistoryService.createPointHistory(
                    EventType.COFFEE_CHAT_CANCEL_REFUND,
                    coffeeChatApp.getMentee().getUserId(),
                    coffeeChatApp.getUsedPoint()
                );
            }

        }

        if (changeStatus == StatusType.APPROVED) {
            // TODO: 채팅룸 생성 (커피챗 시작 시간이 되면 채팅방 활성화)
        }

        coffeeChatApp.setStatus(changeStatus); // 상태 변경

        return CoffeeChatAppStatusResponseDto.from(coffeeChatApp);

    }

    // 커피챗 정보의 작성자가 요청한 사용자와 일치하는지 확인
    private void validateCoffeeChatOwner(Long creatorId, Long requestUserId) {
        if (!creatorId.equals(requestUserId)) {
            throw new CustomException(ErrorCode.NOT_COFFEE_CHAT_INFO_OWNER);
        }
    }


}
