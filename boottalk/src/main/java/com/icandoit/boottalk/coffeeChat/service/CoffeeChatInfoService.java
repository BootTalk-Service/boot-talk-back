package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.UserType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatQueryRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatInfoService {

    private final UserRepository userRepository;
    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final CoffeeChatQueryRepository coffeeChatQueryRepository;

    @Transactional
    public CoffeeChatInfoResponseDto createCoffeeChatInfo(Long userId,
        CoffeeChatInfoRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (coffeeChatInfoRepository.existsByUser_UserId(userId)) {
            throw new CustomException(ErrorCode.COFFEE_CHAT_ALREADY_EXISTS);
        }
        CoffeeChatInfo coffeeChatInfo = CoffeeChatInfo.of(
            user,
            user.getUserName(),
            requestDto.userType(),
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

    @Transactional(readOnly = true) 
    public Page<CoffeeChatListDto> getFilteredCoffeeChatResults(
        @Nullable JobType jobType,
        @Nullable UserType userType,
        Pageable pageable
    ) {
        return coffeeChatQueryRepository.getFilteredCoffeeChatResults(jobType, userType, pageable);
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
        coffeeChatInfoRepository.delete(coffeeChatInfo);
    }

    private CoffeeChatInfo getCoffeeChatInfoByUserId(Long userId) {
        return coffeeChatInfoRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
