package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoffeeChatInfoService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final UserRepository userRepository;

    @Transactional
    public CoffeeChatInfoResponseDto createCoffeeChatInfo(Long userId,
        CoffeeChatInfoRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        CoffeeChatInfo coffeeChatInfo = CoffeeChatInfo.of(
            user,
            requestDto.userType(),
            requestDto.jobType(),
            requestDto.introduction()
        );
        coffeeChatInfo.setUser(user);

        CoffeeChatInfo savedInfo = coffeeChatInfoRepository.save(coffeeChatInfo);
        return CoffeeChatInfoResponseDto.from(savedInfo);
    }

    @Transactional
    public CoffeeChatInfoResponseDto getMyCoffeeChatInfo(Long userId) {
        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return CoffeeChatInfoResponseDto.from(coffeeChatInfo);
    }

    // todo : querydsl 적용하여 다시 하겠습니당..
//    public Page<CoffeeChatInfoResponseDto> searchCoffeeChatInfo(
//        CoffeeChatSearchRequestDto requestDto, Pageable pageable) {
//        Page<CoffeeChatInfo> coffeeChatPage = coffeeChatInfoRepository.findAllBySearch(requestDto, pageable);
//
//        Page<CoffeeChatInfoResponseDto> responsePage = coffeeChatPage.map(CoffeeChatInfoResponseDto::new);
//
//    }


    @Transactional
    public CoffeeChatInfoResponseDto updateMyCoffeeChatInfo(
        Long userId, CoffeeChatInfoRequestDto requestDto) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        coffeeChatInfo.update(requestDto);
        return CoffeeChatInfoResponseDto.from(coffeeChatInfo);
    }

    @Transactional
    public CoffeeChatInfoResponseDto deleteMyCoffeeChatInfo(Long userId) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        coffeeChatInfoRepository.delete(coffeeChatInfo);
        return CoffeeChatInfoResponseDto.from(coffeeChatInfo);
    }
}
