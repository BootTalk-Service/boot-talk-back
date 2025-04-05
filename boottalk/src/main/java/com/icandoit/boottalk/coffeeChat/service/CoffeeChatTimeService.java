package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeListDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatTimeRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatTimeService {

    private final CoffeeChatInfoRepository coffeeChatInfoRepository;
    private final CoffeeChatTimeRepository coffeeChatTimeRepository;

    @Transactional
    public List<CoffeeChatTimeResponseDto> createCoffeeChatTimes(Long userId,
        CoffeeChatTimeListDto requestDto) {

        // todo: 유저조회
        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findBymentor_UserId(userId)
            .orElseThrow(() -> new CustomException(
                ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));

        List<CoffeeChatTime> coffeeChatTimes = new ArrayList<>();
        for (CoffeeChatTimeDto timeDto : requestDto.availableTimes()) {
            CoffeeChatTime time = CoffeeChatTime.of(
                coffeeChatInfo,
                timeDto.dayOfWeek(),
                timeDto.startTime(),
                timeDto.endTime()
            );
            coffeeChatInfo.addAvailableTime(time);
            coffeeChatTimes.add(time);
        }

        coffeeChatTimeRepository.saveAll(coffeeChatTimes);
        return coffeeChatTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CoffeeChatTimeResponseDto> getMyCoffeeChatTimes(Long userId) {
        List<CoffeeChatTime> coffeeChatTimes = getmentorCoffeeChatTimesOrThrow(userId);

        return coffeeChatTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .toList();
    }

    @Transactional
    public List<CoffeeChatTimeResponseDto> updateCoffeeChatTimes(Long userId,
        CoffeeChatTimeListDto requestDto) {
        // 기존 멘토의 커피챗 시간 조회 (없으면 예외 발생)
        List<CoffeeChatTime> existingTimeSlots = getmentorCoffeeChatTimesOrThrow(userId);

        // 새로운 시간 리스트를 CoffeeChatTime 엔티티 리스트로 변환
        CoffeeChatInfo coffeeChatInfo = existingTimeSlots.get(0).getCoffeeChatInfo();
        List<CoffeeChatTime> newTimeSlots = requestDto.availableTimes().stream()
            .map(timeDto -> CoffeeChatTime.of(coffeeChatInfo, timeDto.dayOfWeek(),
                timeDto.startTime(), timeDto.endTime()))
            .toList();

        // 삭제할 시간 찾기 (기존 데이터 중에서 새로운 데이터에 없는 항목)
        List<CoffeeChatTime> toDelete = existingTimeSlots.stream()
            .filter(existing -> !newTimeSlots.contains(existing))
            .toList();

        // 추가할 시간 찾기 (새로운 데이터 중에서 기존 데이터에 없는 항목)
        List<CoffeeChatTime> toAdd = newTimeSlots.stream()
            .filter(newSlot -> !existingTimeSlots.contains(newSlot))
            .toList();

        // 기존 시간 삭제 및 새로운 시간 추가
        coffeeChatTimeRepository.deleteAll(toDelete);
        coffeeChatTimeRepository.saveAll(toAdd);

        return coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(userId).stream()
            .map(CoffeeChatTimeResponseDto::from)
            .toList();
    }

    private List<CoffeeChatTime> getmentorCoffeeChatTimesOrThrow(Long userId) {

        return Optional.ofNullable(
                coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(userId))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));
    }
}
