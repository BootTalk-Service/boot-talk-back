package com.icandoit.boottalk.coffeeChat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationUpdateDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatApplicationService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final CoffeeChatApplicationRepository coffeeChatAppRepository;
    private final UserRepository userRepository;

    @Transactional
    public CoffeeChatApplicationResponseDto createCoffeeChatApp(Long userId, CoffeeChatApplicationCreateDto request) {
        // TODO: 동일한 유저가 동일한 커피챗에 중복 신청 불가능하게 수정
        // TODO: 커피챗 신청 성공 시, 해당 시간대의 커피챗에 다른 사용자가 신청 요청하지 못하도록 동시성 제어 필요

        User user = getUser(userId);
        CoffeeChatInfo coffeeChatInfo = getCoffeeChatInfo(request.coffeeChatInfoId());
        CoffeeChatApplication coffeeChatApp = CoffeeChatApplication.of(user, coffeeChatInfo, request);
        coffeeChatAppRepository.save(coffeeChatApp);

        // TODO: 포인트 차감

        return CoffeeChatApplicationResponseDto.from(coffeeChatApp);

    }

	@Transactional(readOnly = true)
    public List<CoffeeChatApplicationResponseDto> getMyCoffeeChatApps(Long userId) {

        List<CoffeeChatApplication> coffeeChatApps = coffeeChatAppRepository.findByMentee_UserId(userId);

        return coffeeChatApps.stream()
            .map(coffeeChatApp -> CoffeeChatApplicationResponseDto.from(coffeeChatApp))
            .toList();
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
