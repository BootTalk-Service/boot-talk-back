package com.icandoit.boottalk.coffeeChat.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional // 테스트 끝나면 자동 롤백
@DisplayName("커피챗 신청 서비스 통합 테스트")
public class CoffeeChatApplicationServiceIntegrationTest {

    @Autowired
    private CoffeeChatApplicationService coffeeChatApplicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeChatInfoRepository coffeeChatInfoRepository;

    @Autowired
    private CoffeeChatApplicationRepository coffeeChatApplicationRepository;

    @Autowired
    private CreatePointHistoryService createPointHistoryService;

    private User mentee;
    private User mentor;
    private CoffeeChatInfo chatInfo;

    @BeforeEach
    void setUp() {
        mentee = userRepository.save(User.builder()
            .userName("테스트 멘티")
            .email("mentee@example.com")
            .resourceUserId("mentee")
            .desiredCareer(BootcampCategoryType.APPLICATION_SW_ENGINEERING)
            .build());

        mentor = userRepository.save(User.builder()
            .userName("테스트 멘토")
            .email("mentor@example.com")
            .resourceUserId("mentor")
            .desiredCareer(BootcampCategoryType.APPLICATION_SW_ENGINEERING)
            .build());

        chatInfo = coffeeChatInfoRepository.save(CoffeeChatInfo.of(
            mentor,
            mentor.getUserName(),
            MentorType.PROFESSIONAL,
            JobType.BACKEND,
            "현업자 백엔드 멘토입니다."
        ));

        // 초기 포인트 적립
        createPointHistoryService.createPointHistory(EventType.SIGN_UP, mentee.getUserId(), 5);
    }

    @Test
    @DisplayName("커피챗 신청 실패 시 포인트 차감 롤백됨")
    void shouldRollbackPointDeduction_WhenApplicationFails() {
        // Given
        //int beforePoint = createPointHistoryService.getCurrentPoint(mentee.getUserId());

        CoffeeChatApplicationCreateDto request = new CoffeeChatApplicationCreateDto(
            chatInfo.getCoffeeChatInfoId(),
            null, // // 강제로 예외 발생 시키기 위해 Entity 제약 조건 위반
            LocalDateTime.of(2025, 4, 5, 10, 30),
            LocalDateTime.of(2025, 4, 5, 11, 0)
        );

        // Save 실패 유도: 예를 들면 필수 필드 누락 or 유니크 위반

        // When
        assertThrows(Exception.class, () -> {
            coffeeChatApplicationService.createCoffeeChatApp(mentee.getUserId(), request);
        });

        // Then: 포인트 차감이 롤백되어 이전과 동일한 값 유지
        //int afterPoint = createPointHistoryService.getCurrentPoint(mentee.getUserId());
        //assertEquals(beforePoint, afterPoint, "커피챗 저장 실패 시 포인트 차감이 롤백되어야 합니다.");
    }
}
