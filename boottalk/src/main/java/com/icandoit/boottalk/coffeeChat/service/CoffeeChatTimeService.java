package com.icandoit.boottalk.coffeeChat.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.icandoit.boottalk.coffeeChat.dto.AvailableChatTimeDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatTimeDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatTime;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatTimeRepository;
import com.icandoit.boottalk.coffeeChat.service.converter.CoffeeChatTimeConverter;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoffeeChatTimeService {
    private static final int COFFEE_CHAT_APPLICATION_PERIOD_DAYS = 30;

    private final CoffeeChatTimeRepository coffeeChatTimeRepository;
    private final CoffeeChatApplicationRepository coffeeChatAppRepository;

    public Map<String, List<String>> createCoffeeChatTimes(Map<String, List<String>> times, CoffeeChatInfo coffeeChatInfo) {

        // 시 : 분 파싱
        List<CoffeeChatTimeDto> timeDtos = CoffeeChatTimeConverter.mapToDtoList(times);

        List<CoffeeChatTime> chatTimes = timeDtos.stream()
            .map(dto -> {
                CoffeeChatTime time = CoffeeChatTime.of(coffeeChatInfo, dto.dayOfWeek(),
                    dto.startTime());
                coffeeChatInfo.addAvailableTime(time);
                return time;
            })
            .toList();

        coffeeChatTimeRepository.saveAll(chatTimes);

        return getTimeSlotsByDay(chatTimes);
    }

    // 자신의 멘토 가능 시간 조회
    public Map<String, List<String>> getMentorAvailableChatTimes(Long userId) {
        return getTimeSlotsByDay(getMentorCoffeeChatTimesOrThrow(userId));
    }

    public AvailableChatTimeDto getAvailableChatTimes(Long coffeeChatInfoId) {

        LocalDateTime now = LocalDateTime.now();
        
        // 멘토가 설정한 요일별 멘토링(커피챗) 시간 목록
        List<CoffeeChatTime> mentoringTimeList = coffeeChatTimeRepository.findAllWithCoffeeChatInfoByCoffeeChatInfoId(coffeeChatInfoId);

        // 이미 신청된 커피챗 시간 목록
        List<LocalDateTime> appliedDateTimes = coffeeChatAppRepository.findStartTimesByCoffeeChatInfoIdAndPeriod(
                coffeeChatInfoId, now, now.plusDays(COFFEE_CHAT_APPLICATION_PERIOD_DAYS));

        // 신청된 시간 제외한 신청 가능한 시간 필터링
        Map<LocalDate, List<LocalTime>> availableChatTimesByDate = getAvailableChatTimesByDate(mentoringTimeList, appliedDateTimes, now);

        Map<LocalDate, List<String>> formattedAvailableChatTimes = availableChatTimesByDate.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                    .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    .collect(Collectors.toList())
            ));

        return new AvailableChatTimeDto(formattedAvailableChatTimes);
    }

    public Map<String, List<String>> updateCoffeeChatTimes(Long userId, Map<String, List<String>> times, CoffeeChatInfo coffeeChatInfo) {

        List<CoffeeChatTime> existingTimes =
            coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(userId);

        // 변환된 새 요청 리스트
        List<CoffeeChatTimeDto> newDtos = CoffeeChatTimeConverter.mapToDtoList(times);

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
        return getTimeSlotsByDay(coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(userId));
    }

    private String generateKey(DayOfWeek dayOfWeek, LocalTime startTime) {
        return dayOfWeek.toString() + "-" + startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private List<CoffeeChatTime> getMentorCoffeeChatTimesOrThrow(Long userId) {

        return Optional.ofNullable(
                coffeeChatTimeRepository.findAllWithCoffeeChatInfoByUserId(userId))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_COFFEE_CHAT_NOT_FOUND));
    }

    private Map<String, List<String>> getTimeSlotsByDay(List<CoffeeChatTime> coffeeChatTimes) {
        return coffeeChatTimes.stream()
            .collect(Collectors.groupingBy(
                time -> time.getDayOfWeek().toString(),
                Collectors.mapping(
                    time -> time.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    Collectors.toList()
                )
            ));
    }

    // 멘토링 가능한 시간과 신청된 시간을 필터링하여 신청 가능한 시간을 구함
    private Map<LocalDate, List<LocalTime>> getAvailableChatTimesByDate(
        List<CoffeeChatTime> mentoringTimeList,
        List<LocalDateTime> appliedDateTimes,
        LocalDateTime now
    ) {
        // 현재 시간 기준으로 신청 기간 설정
        LocalDate startDate = now.toLocalDate();
        LocalDate endDate = startDate.plusDays(COFFEE_CHAT_APPLICATION_PERIOD_DAYS);

        // 신청일 기준 30일 내 멘토링 가능한 요일의 날짜를 key로, 해당 날짜의 신청 가능 시간을 리스트로 매핑
        Map<LocalDate, List<LocalTime>> availableChatTimesByDate = new HashMap<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<LocalTime> availableTimes = new ArrayList<>();
            for (CoffeeChatTime chatTime : mentoringTimeList) {
                if (date.getDayOfWeek() == chatTime.getDayOfWeek()) { // 멘토링 시간의 요일과 일치하는 날짜라면
                    LocalTime startTime = chatTime.getStartTime();
                    LocalDateTime fullDateTime = LocalDateTime.of(date, startTime); // appliedDateTimes의 LocalDateTime 과 비교하기 위해 포맷팅

                    if(fullDateTime.isBefore(now)) continue;
                    if (!appliedDateTimes.contains(fullDateTime)) { // 이미 신청된 시간이 아니라면 추가
                        availableTimes.add(startTime);
                    }
                }
            }

            // 신청 가능한 시간이 있다면 맵에 추가
            if (!availableTimes.isEmpty()) {
                availableChatTimesByDate.put(date, availableTimes);
            }
        }

        return availableChatTimesByDate;
    }

}
