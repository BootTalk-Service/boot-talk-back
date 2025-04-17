package com.icandoit.boottalk.coffeeChat.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoffeeChatCommonService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final CoffeeChatApplicationRepository coffeeChatAppRepository;
    private final UserRepository userRepository;

    protected User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    protected CoffeeChatInfo getCoffeeChatInfo(Long coffeeChatInfoId) {
        return coffeeChatInfoRepository.findById(coffeeChatInfoId)
            .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_NOT_FOUND));
    }

    protected CoffeeChatInfo getCoffeeChatInfoByUserId(Long userId) {
        return coffeeChatInfoRepository.findByMentor_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));
    }

    protected Optional<CoffeeChatInfo> getOptionalCoffeeChatInfoByUserId(Long userId) {
        return coffeeChatInfoRepository.findByMentor_UserId(userId);
    }

    protected CoffeeChatApplication getCoffeeChatApplication(Long coffeeChatAppId) {
        return coffeeChatAppRepository.findById(coffeeChatAppId)
            .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_APPLICATION_NOT_FOUND));
    }

    // 해당 사용자가 작성한 커피챗 신청자인지 확인
    protected void validateCoffeeChatApplicant(Long userId, Long menteeId) {
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

    // 커피챗 정보의 작성자가 요청한 사용자와 일치하는지 확인
    protected void validateCoffeeChatOwner(Long creatorId, Long requestUserId) {
        if (!creatorId.equals(requestUserId)) {
            throw new CustomException(ErrorCode.NOT_COFFEE_CHAT_INFO_OWNER);
        }
    }

}
