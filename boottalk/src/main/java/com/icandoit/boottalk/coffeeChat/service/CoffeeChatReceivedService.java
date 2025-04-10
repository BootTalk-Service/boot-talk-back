package com.icandoit.boottalk.coffeeChat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatAppChangeStatusDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatAppStatusResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatReceivedService {

    private final CoffeeChatApplicationRepository coffeeChatAppRepository;

    private final CoffeeChatInfoService coffeeChatInfoService;
    private final CoffeeChatApplicationService coffeeChatAppService;

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

        Long mentoId = coffeeChatApp.getCoffeeChatInfo().getMentor().getUserId();

        validateCoffeeChatOwner(mentoId, userId);

        StatusType changeStatus = request.changeStatus();
        coffeeChatApp.setStatus(changeStatus); // 상태 변경

        if (changeStatus == StatusType.REJECTED) {
            // TODO: 멘티가 커피챗 신청 시 차감 되었던 포인트 회수 처리 추가
        } else if (changeStatus == StatusType.APPROVED) {
            // TODO: 채팅룸 생성 (커피챗 시작 시간이 되면 채팅방 활성화)
        }
        return CoffeeChatAppStatusResponseDto.from(coffeeChatApp);

    }

    // 커피챗 정보의 작성자가 요청한 사용자와 일치하는지 확인
    private void validateCoffeeChatOwner(Long creatorId, Long requestUserId) {
        if (!creatorId.equals(requestUserId)) {
            throw new CustomException(ErrorCode.NOT_COFFEE_CHAT_INFO_OWNER);
        }
    }


}
