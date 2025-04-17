package com.icandoit.boottalk.coffeeChat.service;

import org.springframework.stereotype.Service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatInfoService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;

    private final CoffeeChatCommonService coffeeChatCommonService;

    public CoffeeChatInfoResponseDto createCoffeeChatInfo(Long userId,
        CoffeeChatInfoRequestDto requestDto) {

        User user = coffeeChatCommonService.getUser(userId);

        if (coffeeChatInfoRepository.existsByMentor_UserId(userId)) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_ALREADY_EXISTS);
        }

        CoffeeChatInfo coffeeChatInfo = CoffeeChatInfo.of(
            user,
            user.getUserName(),
            requestDto.mentorType(),
            requestDto.jobType(),
            requestDto.introduction()
        );
        CoffeeChatInfo savedInfo = coffeeChatInfoRepository.save(coffeeChatInfo);
        return CoffeeChatInfoResponseDto.from(savedInfo);
    }

    public CoffeeChatInfoResponseDto getMyCoffeeChatInfo(Long userId) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatCommonService.getCoffeeChatInfoByUserId(userId);
        return CoffeeChatInfoResponseDto.from(coffeeChatInfo);
    }

    public CoffeeChatInfoResponseDto getCoffeeChatInfo(Long coffeeChatInfoId) {
        return CoffeeChatInfoResponseDto.from(
            coffeeChatInfoRepository.findById(coffeeChatInfoId)
                .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_NOT_FOUND))
        );
    }

    public CoffeeChatInfoResponseDto updateMyCoffeeChatInfo(
        Long userId, CoffeeChatInfoRequestDto requestDto) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatCommonService.getCoffeeChatInfoByUserId(userId);

        coffeeChatInfo.update(requestDto);
        return CoffeeChatInfoResponseDto.from(coffeeChatInfo);
    }

    public void deleteMyCoffeeChatInfo(Long userId) {
        CoffeeChatInfo coffeeChatInfo = coffeeChatCommonService.getCoffeeChatInfoByUserId(userId);

        // 멘토링 활동 금지 당한 커피챗 정보는 삭제 불가
        if (coffeeChatInfo.isMentoringBanned()) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_DELETE_BANNED);
        }

        coffeeChatInfoRepository.delete(coffeeChatInfo);
    }

}
