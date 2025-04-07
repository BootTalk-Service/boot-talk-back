package com.icandoit.boottalk.coffeeChat.service;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeMapDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatTimeRepository;
import com.icandoit.boottalk.coffeeChat.service.converter.CoffeeChatTimeConverter;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
        CoffeeChatTimeMapDto requestDto) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findBymentor_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));

        // 이미 시간이 존재한다면 예외처리
        if (coffeeChatTimeRepository.existsByCoffeeChatInfo(coffeeChatInfo)) {
            throw new CustomException(ErrorCode.ALREADY_CREATED_COFFEE_CHAT_TIME);
        }

        // 시 : 분 파싱
        List<CoffeeChatTimeDto> timeDtos = CoffeeChatTimeConverter.toDtoList(requestDto);

        List<CoffeeChatTime> chatTimes = timeDtos.stream()
            .map(dto -> {
                CoffeeChatTime time = CoffeeChatTime.of(coffeeChatInfo, dto.dayOfWeek(),
                    dto.startTime());
                coffeeChatInfo.addAvailableTime(time);
                return time;
            })
            .toList();

        coffeeChatTimeRepository.saveAll(chatTimes);

        return chatTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .toList();
    }

    // 자신의 멘토 가능 시간 조회
    @Transactional(readOnly = true)
    public List<CoffeeChatTimeResponseDto> getMentorAvailableChatTimes(Long userId) {
        List<CoffeeChatTime> coffeeChatTimes = getmentorCoffeeChatTimesOrThrow(userId);

        return coffeeChatTimes.stream()
            .map(CoffeeChatTimeResponseDto::from)
            .toList();
    }

    @Transactional
    public List<CoffeeChatTimeResponseDto> updateCoffeeChatTimes(Long userId,
        CoffeeChatTimeMapDto requestDto) {

        CoffeeChatInfo coffeeChatInfo = coffeeChatInfoRepository.findBymentor_UserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));

        List<CoffeeChatTime> existingTimes = coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(
            userId);

        // 변환된 새 요청 리스트
        List<CoffeeChatTimeDto> newDtos = CoffeeChatTimeConverter.toDtoList(requestDto);

        // Set으로 중복 제거 및 비교를 쉽게
        Set<String> existingKeys = existingTimes.stream()
            .map(time -> generateKey(time.getDayOfWeek(), time.getStartTime()))
            .collect(Collectors.toSet());

        Set<String> newKeys = newDtos.stream()
            .map(dto -> generateKey(dto.dayOfWeek(), dto.startTime()))
            .collect(Collectors.toSet());

        // 삭제 대상: 기존엔 있었는데, 새 요청에는 없는 것
        List<CoffeeChatTime> toDelete = existingTimes.stream()
            .filter(
                time -> !newKeys.contains(generateKey(time.getDayOfWeek(), time.getStartTime())))
            .toList();

        // 추가 대상: 새 요청에는 있는데 기존에는 없는 것
        List<CoffeeChatTime> toAdd = newDtos.stream()
            .filter(dto -> !existingKeys.contains(generateKey(dto.dayOfWeek(), dto.startTime())))
            .map(dto -> {
                CoffeeChatTime time = CoffeeChatTime.of(coffeeChatInfo, dto.dayOfWeek(),
                    dto.startTime());
                coffeeChatInfo.addAvailableTime(time);
                return time;
            })
            .toList();

        if (!toDelete.isEmpty()) {
            coffeeChatTimeRepository.deleteAll(toDelete);
        }
        if (!toAdd.isEmpty()) {
            coffeeChatTimeRepository.saveAll(toAdd);
        }

        // 최종 조회된 전체 시간 목록 반환
        List<CoffeeChatTime> finalList = coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(
            userId);
        return finalList.stream().map(CoffeeChatTimeResponseDto::from).toList();
    }

    private String generateKey(DayOfWeek dayOfWeek, LocalTime startTime) {
        return dayOfWeek.toString() + "-" + startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private List<CoffeeChatTime> getmentorCoffeeChatTimesOrThrow(Long userId) {

        return Optional.ofNullable(
                coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(userId))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));
    }
}
