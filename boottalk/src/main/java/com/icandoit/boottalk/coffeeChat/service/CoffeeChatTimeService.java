package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatTimeRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import jakarta.validation.Valid;
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
        @Valid CoffeeChatTimeRequestDto requestDto) {

        // todo: 유저조회
        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findByMento_UserId(userId)
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
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CoffeeChatTimeResponseDto> getMyCoffeeChatTimes(Long userId) {
        List<CoffeeChatTime> coffeeChatTimes = getCoffeeChatTimesOrThrow(userId);

        return coffeeChatTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .toList();
    }

    @Transactional
    public List<CoffeeChatTimeResponseDto> updateCoffeeChatTimes(Long mentoId,
        CoffeeChatTimeListDto requestDto) {
        // 기존 멘토의 커피챗 시간 조회 (없으면 예외 발생)
        List<CoffeeChatTime> existingTimeSlots = getMentoCoffeeChatTimesOrThrow(mentoId);

        // 새로운 시간 리스트를 CoffeeChatTime 엔티티 리스트로 변환
        CoffeeChatInfo coffeeChatInfo = existingTimeSlots.get(0).getCoffeeChatInfo();
        List<CoffeeChatTime> newTimeSlots = requestDto.availableTimes().stream()
            .map(timeDto -> CoffeeChatTime.of(coffeeChatInfo, timeDto.dayOfWeek(),
                timeDto.startTime(), timeDto.endTime()))
            .toList();

        // 기존과 비교 후 존재하지 않을 경우 toDelete 리스트에 추가
        List<CoffeeChatTime> toDelete = existingTimeSlots.stream()
            .filter(
                existing -> newTimeSlots.stream()
                    .noneMatch(newSlot -> newSlot.equals(existing)))
            .toList();

        // 기존과 비교 후 새롭게 추가된 시간을 toAdd 리스트에 추가
        List<CoffeeChatTime> toAdd = newTimeSlots.stream()
            .filter(newSlot -> existingTimeSlots.stream()
                .noneMatch(existing -> existing.equals(newSlot)))
            .toList();

        // 기존 시간 삭제 및 새로운 시간 추가
        coffeeChatTimeRepository.deleteAll(toDelete);
        coffeeChatTimeRepository.saveAll(toAdd);

        return coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(mentoId).stream()
            .map(CoffeeChatTimeResponseDto::from)
            .toList();
    }

    private List<CoffeeChatTime> getMentoCoffeeChatTimesOrThrow(Long mentoId) {

        return Optional.ofNullable(
                coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(mentoId))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));
    }
}
