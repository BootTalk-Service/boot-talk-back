package com.icandoit.boottalk.coffeeChat.service;

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
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.common.dto.PagedResponseDto;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeeChatApplicationService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final CoffeeChatApplicationRepository coffeeChatAppRepository;
    private final UserRepository userRepository;

    private final CreatePointHistoryService createPointHistoryService;

    // 멘토 타입에 따른 포인트 차감액
    private final int GENERAL_COFFEE_CHAT_POINT = 1;
    private final int GRADUATE_COFFEE_CHAT_POINT = 2;
    private final int PROFESSIONAL_COFFEE_CHAT_POINT = 3;

    @Transactional
    public CoffeeChatApplicationResponseDto createCoffeeChatApp(Long userId, CoffeeChatApplicationCreateDto request) {

        Long coffeChatInfoId = request.coffeeChatInfoId();
        
        if (coffeeChatAppRepository.existsByMentee_UserIdAndCoffeeChatInfo_CoffeeChatInfoId(userId, coffeChatInfoId)) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_ALREADY_EXISTS);
        }
        
        log.debug("커피챗 신청 Lock : userId: {}", userId);
        // 커피챗 신청 시, 해당 시간대의 커피챗에 다른 사용자가 신청 요청하지 못하도록 동시성 제어
        if (coffeeChatAppRepository.existsByCoffeeChatInfo_CoffeeChatInfoIdAndCoffeeChatStartTime(
            coffeChatInfoId, request.coffeeChatStartTime())
        ) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_TIME_ALREADY_EXISTS);
        }
        log.debug("커피챗 신청 Lock 해제 : userId: {}", userId);


        User user = getUser(userId);
        CoffeeChatInfo coffeeChatInfo = getCoffeeChatInfo(coffeChatInfoId);

        // 멘토 타입에 따른 커피챗 포인트 차감. 포인트 부족 시 커피챗 신청 실패 처리
        MentorType mentorType = coffeeChatInfo.getMentorType();
        int currentPoint = createPointHistoryService.getCurrentPoint(userId);
        int deductionPoint = getPointCostByMentorType (mentorType);

        log.info("userId: {}, mentorType: {}, currentPoint: {}, deductionPoint: {}",
            userId, mentorType, currentPoint, deductionPoint);

        if (currentPoint < deductionPoint) {
            throw new CustomException(ErrorCode.INSUFFICIENT_POINT_FOR_CHAT);
        }


        createPointHistoryService.createPointHistory(EventType.COFFEE_CHAT_APPLY, userId, deductionPoint);

        CoffeeChatApplication coffeeChatApp = CoffeeChatApplication.of(user, coffeeChatInfo, request);
        coffeeChatAppRepository.save(coffeeChatApp);

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


    @Transactional
    public CoffeeChatApplicationResponseDto updateCoffeeChatApp(
        Long userId, Long coffeeChatAppId, CoffeeChatApplicationUpdateDto request) {
        CoffeeChatApplication coffeeChatApp = getCoffeeChatApplication(coffeeChatAppId);

        validateCoffeeChatApplicant(userId, coffeeChatApp.getMentee().getUserId());
        validateCoffeeChatInfo(coffeeChatApp.getCoffeeChatInfo().getCoffeeChatInfoId());

        // 상태가 대기 중일 때만 content 수정 가능
        if (coffeeChatApp.getStatus() != StatusType.PENDING) {
            throw new CustomException(ErrorCode.NOT_PENDING_STATUS);
        }

        coffeeChatApp.setContent(request.content());

        return CoffeeChatApplicationResponseDto.from(coffeeChatApp);
    }

    @Transactional
    public void deleteCoffeeChatApp(Long userId, Long coffeeChatAppId) {
        CoffeeChatApplication coffeeChatApp = getCoffeeChatApplication(coffeeChatAppId);

        validateCoffeeChatApplicant(userId, coffeeChatApp.getMentee().getUserId());
        validateCoffeeChatInfo(coffeeChatApp.getCoffeeChatInfo().getCoffeeChatInfoId());

        coffeeChatAppRepository.delete(coffeeChatApp);
    }


    private User getUser(Long userId) { // TODO: 추후 공통 코드 관리하는 곳으로 분리하여 호출하도록 리팩토링 필요해보임
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private CoffeeChatInfo getCoffeeChatInfo(Long coffeeChatInfoId) {
        return coffeeChatInfoRepository.findById(coffeeChatInfoId)
            .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_NOT_FOUND));
    }

    protected CoffeeChatApplication getCoffeeChatApplication(Long coffeeChatAppId) {
        return coffeeChatAppRepository.findById(coffeeChatAppId)
            .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_NOT_FOUND));
    }

    // 해당 사용자가 작성한 커피챗 신청자인지 확인
    private void validateCoffeeChatApplicant(Long userId, Long menteeId) {
        if (!userId.equals(menteeId)) {
            throw new CustomException(ErrorCode.NOT_COFFEE_CHAT_APPLICATION_OWNER);
        }
    }

    // 존재하는 커피챗 정보인지 확인
    protected void validateCoffeeChatInfo(Long coffeeChatInfoId) {
        if(!coffeeChatInfoRepository.existsById(coffeeChatInfoId)) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_NOT_FOUND);
        }
    }


}
