package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatSearchRequestDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.dto.SuccessResponseDto;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoffeeChatInfoService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;

    @Transactional
    public SuccessResponseDto<CoffeeChatInfoResponseDto> createCoffeeChatInfo(
        CoffeeChatInfoRequestDto requestDto) {

        CoffeeChatInfo coffeeChatInfo = CoffeeChatInfo.of(
            // userID
            requestDto.userType,
            requestDto.jobType,
            requestDto.introduction
        );
        CoffeeChatInfo savedInfo = coffeeChatInfoRepository.save(coffeeChatInfo);
        return SuccessResponseDto.of("커피챗 등록의 성공하였습니다.", CoffeeChatInfoResponseDto.from(savedInfo));
    }

    @Transactional
    public SuccessResponseDto<CoffeeChatInfoResponseDto> getMyCoffeeChatInfo(Long userId) {
        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return SuccessResponseDto.of("내 커피챗 조회를 성공하였습니다.",
            CoffeeChatInfoResponseDto.from(coffeeChatInfo));
    }

    // todo : querydsl 적용하여 다시 하겠습니당..
//    public SuccessResponseDto<Page<CoffeeChatInfoResponseDto>> searchCoffeeChatInfo(
//        CoffeeChatSearchRequestDto requestDto, Pageable pageable) {
//        Page<CoffeeChatInfo> coffeeChatPage = coffeeChatInfoRepository.findAllBySearch(requestDto, pageable);
//
//        Page<CoffeeChatInfoResponseDto> responsePage = coffeeChatPage.map(CoffeeChatInfoResponseDto::new);
//
//    }


    @Transactional
    public SuccessResponseDto<CoffeeChatInfoResponseDto> updateMyCoffeeChatInfo(
        Long userId, CoffeeChatInfoRequestDto requestDto) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        coffeeChatInfo.update(requestDto);
        return SuccessResponseDto.of("커피챗 정보가 수정되었습니다.",
            CoffeeChatInfoResponseDto.from(coffeeChatInfo));
    }

    @Transactional
    public SuccessResponseDto<CoffeeChatInfoResponseDto> deleteMyCoffeeChatInfo(Long userId) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (coffeeChatInfo.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_COFFEE_CHAT_INFO);
        }

        coffeeChatInfo.deleted();
        return SuccessResponseDto.of("커피챗 정보가 삭제되었습니다.",
            CoffeeChatInfoResponseDto.from(coffeeChatInfo));
    }
}
