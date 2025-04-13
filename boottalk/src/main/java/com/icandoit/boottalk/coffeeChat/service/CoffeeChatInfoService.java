package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatInfoService {

    private final UserRepository userRepository;
    private final CoffeeChatInfoRepository coffeeChatInfoRepository;

    @Transactional
    public CoffeeChatInfoResponseDto createCoffeeChatInfo(Long userId,
        CoffeeChatInfoRequestDto requestDto) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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

    @Transactional
    public CoffeeChatInfoResponseDto getMyCoffeeChatInfo(Long userId) {

        CoffeeChatInfo coffeeChatInfo = getCoffeeChatInfoByUserId(userId);
        return CoffeeChatInfoResponseDto.from(coffeeChatInfo);
    }

    public CoffeeChatInfoResponseDto getCoffeeChatInfo(Long coffeeChatInfoId) {
        return CoffeeChatInfoResponseDto.from(
            coffeeChatInfoRepository.findById(coffeeChatInfoId)
                .orElseThrow(() -> new CustomException(ErrorCode.COFFEE_CHAT_NOT_FOUND))
        );
    }

    @Transactional
    public CoffeeChatInfoResponseDto updateMyCoffeeChatInfo(
        Long userId, CoffeeChatInfoRequestDto requestDto) {

        CoffeeChatInfo coffeeChatInfo = getCoffeeChatInfoByUserId(userId);

        coffeeChatInfo.update(requestDto);
        return CoffeeChatInfoResponseDto.from(coffeeChatInfo);
    }

    @Transactional
    public void deleteMyCoffeeChatInfo(Long userId) {
        CoffeeChatInfo coffeeChatInfo = getCoffeeChatInfoByUserId(userId);

        // 멘토링 활동 금지 당한 커피챗 정보는 삭제 불가
        if (coffeeChatInfo.isMentoringBanned()) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_DELETE_BANNED);
        }

        coffeeChatInfoRepository.delete(coffeeChatInfo);
    }

    protected CoffeeChatInfo getCoffeeChatInfoByUserId(Long userId) {
        return coffeeChatInfoRepository.findByMentor_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));
    }
}
