package com.icandoit.boottalk.coffeeChat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.point_history.domain.repository.PointHistoryRepository;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

/**
 * *** 주의 ****
 * 테스트 실행 후, @AfterEach를 통해 모든 데이터를 deleteALl()로 삭제합니다.
 * 실제 데이터 손실을 방지하기 위해 반드시 **테스트 전용 DB**에서 실행하세요!!
 */
@SpringBootTest
public class CoffeeChatApplicationConcurrencyTest {

    private final int THREAD_COUNT = 1000;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    @Autowired
    private CoffeeChatApplicationService coffeeChatApplicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeChatInfoRepository coffeeChatInfoRepository;

    @Autowired
    private CoffeeChatApplicationRepository coffeeChatApplicationRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private CreatePointHistoryService createPointHistoryService;

    private List<User> menteeList;
    private User mentor;
    private CoffeeChatInfo chatInfo;

    @BeforeEach
    void setUp() {
        menteeList = new ArrayList<>();

        String mentorName = "test_mentor";
        mentor = userRepository.findByUserName(mentorName)
            .orElseGet(() -> userRepository.save(
                User.builder()
                    .userName(mentorName)
                    .email(mentorName)
                    .resourceUserId(mentorName)
                    .desiredCareer(BootcampCategoryType.APPLICATION_SW_ENGINEERING)
                    .build()
            ));

        chatInfo = coffeeChatInfoRepository.save(CoffeeChatInfo.of(
            mentor,
            mentor.getUserName(),
            MentorType.PROFESSIONAL,
            JobType.BACKEND,
            "현업자 백엔드 멘토입니다."
        ));

        for (int i = 0; i < THREAD_COUNT; i++) {
            String menteeName = "test_mentee" + (i + 1);

            // 유저가 이미 있는지 확인
            User mentee = userRepository.findByUserName(menteeName)
                .orElseGet(() -> {
                    // 없으면 새로 생성
                    User newUser = userRepository.save(
                        User.builder()
                            .userName(menteeName)
                            .email(menteeName)
                            .resourceUserId(menteeName)
                            .desiredCareer(BootcampCategoryType.APPLICATION_SW_ENGINEERING)
                            .build());

                    // 초기 포인트 적립
                    createPointHistoryService.createPointHistory(EventType.SIGN_UP, newUser.getUserId(), 5);

                    return newUser;
                });

            menteeList.add(mentee);
        }

    }

    @AfterEach
    void cleanUp() {
        coffeeChatApplicationRepository.deleteAll();
        coffeeChatInfoRepository.deleteAll();
        pointHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("동일한 커피챗의 동일한 시간대에 여러 명이 신청 시, 1명만 신청 할 수 있음")
    public void onlyOneUserCanApplyForSameTimeSlotInSameCoffeeChat() throws InterruptedException {

        // 테스트 대상 요청 DTO 생성 (동일한 시간대에 대해 여러 명이 신청 시도)
        CoffeeChatApplicationCreateDto request = new CoffeeChatApplicationCreateDto(
            chatInfo.getCoffeeChatInfoId(),
            "커피챗 신청합니다.",
            LocalDateTime.of(2025, 4, 5, 10, 30),
            LocalDateTime.of(2025, 4, 5, 11, 0)
        );

        // 모든 스레드가 작업을 완료할 떄 까지 기다리기 위한 CountDownLatch
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // 각 스레드의 실행 결과(Future)를 담기 위한 리스트
        // Future는 비동기 작업의 결과를 나중에 조회할 수 있도록 도와주는 객체
        List<Future<CoffeeChatApplicationResponseDto>> futures = new ArrayList<>();

        // THREAD_COUNT 수 만큼의 멘티가 동시에 같은 시간대의 커피챗을 신청 시도
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int menteeNumber = i;

            // submit() 은 작업 결과를 받을 수 있는 Fure 를 반환함
            Future<CoffeeChatApplicationResponseDto> future = executorService.submit(() -> {
                try {
                    // 각 멘티가 커피챗 신청 시도
                    return coffeeChatApplicationService.createCoffeeChatApp(
                        menteeList.get(menteeNumber).getUserId(), request);
                } finally {
                    // 현재 스레드 완료 ->  latch 감소
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        // 모든 스레드가 작업을 마칠 때까지 대기
        latch.await();

        int successCount = 0;
        int failCount = 0;
        // 각 스레드의 결과 확인
        for (Future<CoffeeChatApplicationResponseDto> future: futures) {
            try{
                CoffeeChatApplicationResponseDto result = future.get();
                System.out.println("신청 성공 : " + result);
                successCount++;
            }catch (ExecutionException e){
                failCount++;
                Throwable cause = e.getCause();
                if (cause instanceof CustomException customEx) {
                    System.out.println("신청 실패 : " + customEx.getErrorCode().name());
                }
            } catch (Exception e){
                e.printStackTrace();
             }
        }

        System.out.println("=== 최종 결과 ====");
        System.out.println("성공 건수: " + successCount);
        System.out.println("실패 건수: " + failCount);

        Assertions.assertEquals(1, successCount); // 딱 한 명만 성공해야 함
        Assertions.assertEquals(THREAD_COUNT - 1, failCount);


    }



}
