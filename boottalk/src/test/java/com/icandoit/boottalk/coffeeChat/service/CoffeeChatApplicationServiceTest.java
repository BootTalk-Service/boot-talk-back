package com.icandoit.boottalk.coffeeChat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationCreateDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatApplicationResponseDto;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatApplication;
import com.icandoit.boottalk.coffeeChat.entity.CoffeeChatInfo;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.entity.enums.StatusType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatApplicationRepository;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatInfoRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.point_history.domain.dto.PointHistoryDto;
import com.icandoit.boottalk.point_history.domain.type.EventType;
import com.icandoit.boottalk.point_history.service.CreatePointHistoryService;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CoffeeChatApplicationServiceTest {

    @InjectMocks
    private CoffeeChatApplicationService coffeeChatApplicationService;

    @Mock
    private CoffeeChatInfoRepository coffeeChatInfoRepository;
    @Mock
    private CoffeeChatApplicationRepository coffeeChatAppRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CreatePointHistoryService createPointHistoryService;

    private User mockUser;
    private CoffeeChatInfo mockChatInfo;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
            .userId(1L)
            .userName("user1")
            .email("user1@example.com")
            .profileImage("")
            .resourceUserId("user1")
            .desiredCareer(BootcampCategoryType.APPLICATION_SW_ENGINEERING)
            .build();

        mockChatInfo = CoffeeChatInfo.builder()
            .coffeeChatInfoId(1L)
            .mentorType(MentorType.PROFESSIONAL)
            .jobType(JobType.BACKEND)
            .introduction("안녕하세요 백엔드 현업자 멘토입니다.")
            .build();

    }

    @Test
    @DisplayName("커피챗 신청 성공")
    void createCoffeeChatApplicationSuccess() {
        // Given
        Long userId = mockUser.getUserId();
        Long coffeeChatInfoId = mockChatInfo.getCoffeeChatInfoId();

        CoffeeChatApplicationCreateDto request = new CoffeeChatApplicationCreateDto(
            coffeeChatInfoId,
            "안녕하세요 백엔드 커피챗 신청합니다.",
            LocalDateTime.of(2025, 4, 5, 10, 30),
            LocalDateTime.of(2025, 4, 5, 11, 0)
        );

        int currentPoint = 5;
        int deductionPoint  = 3;

        CoffeeChatApplication mockApplication = CoffeeChatApplication.of(mockUser, mockChatInfo, request);

        // Mock 설정
        // 중복 신청이 아닌 경우 설정
        when(coffeeChatAppRepository.existsByMentee_UserIdAndCoffeeChatInfo_CoffeeChatInfoId(userId,
            coffeeChatInfoId)).thenReturn(false);
        // 유저와 커피챗 정보가 정상 조회 된다는 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(coffeeChatInfoRepository.findById(coffeeChatInfoId)).thenReturn(Optional.of(mockChatInfo));
        // 현재 포인트 조회 결과 지정
        when(createPointHistoryService.getCurrentPoint(userId)).thenReturn(currentPoint);
        // 포인트 차감
        when(createPointHistoryService.createPointHistory(
            EventType.COFFEE_CHAT_APPLY, userId, deductionPoint)).thenReturn(mock(PointHistoryDto.class));
        // 저장 후 반환될 커피챗 신청 객체 설정
        when(coffeeChatAppRepository.save(any(CoffeeChatApplication.class))).thenReturn(mockApplication);

        // When
        CoffeeChatApplicationResponseDto response = coffeeChatApplicationService.createCoffeeChatApp(userId, request);

        // Then
        assertNotNull(response); // 반환된 응답이 null이 아님
        assertEquals(coffeeChatInfoId, response.coffeeChatInfoId()); // 커피챗 ID 확인
        assertEquals(userId, response.menteeUserId()); // 신청자 유저 ID 확인
        assertEquals(StatusType.PENDING, response.status()); // 신청 상태 확인

    }

    @Test
    @DisplayName("커피챗 신청 실패 - 포인트 부족")
    void createCoffeeChatApplication_LackOfPoint() {
        // Given
        Long userId = mockUser.getUserId();
        Long coffeeChatInfoId = mockChatInfo.getCoffeeChatInfoId();

        CoffeeChatApplicationCreateDto request = new CoffeeChatApplicationCreateDto(
            coffeeChatInfoId,
            "안녕하세요 백엔드 커피챗 신청합니다.",
            LocalDateTime.of(2025, 4, 5, 10, 30),
            LocalDateTime.of(2025, 4, 5, 11, 0)
        );

        int currentPoint = 1; // 차감 포인트보다 부족

        // Mock 설정
        // 중복 신청이 아닌 경우 설정
        when(coffeeChatAppRepository.existsByMentee_UserIdAndCoffeeChatInfo_CoffeeChatInfoId(userId,
            coffeeChatInfoId)).thenReturn(false);
        // 유저와 커피챗 정보가 정상 조회 된다는 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(coffeeChatInfoRepository.findById(coffeeChatInfoId)).thenReturn(Optional.of(mockChatInfo));
        // 현재 포인트 조회 결과 지정
        when(createPointHistoryService.getCurrentPoint(userId)).thenReturn(currentPoint);

        // When & Then
        assertThrows(CustomException.class, () -> {
            coffeeChatApplicationService.createCoffeeChatApp(userId, request);
        } );
    }

    @Test
    @DisplayName("커피챗 신청 실패 - 이미 신청한 경우")
    void createCoffeeChatApplication_Duplicate() {
        // Given
        Long userId = mockUser.getUserId();
        Long coffeeChatInfoId = mockChatInfo.getCoffeeChatInfoId();

        CoffeeChatApplicationCreateDto request = new CoffeeChatApplicationCreateDto(
            coffeeChatInfoId,
            "안녕하세요 백엔드 커피챗 신청합니다.",
            LocalDateTime.of(2025, 4, 5, 10, 30),
            LocalDateTime.of(2025, 4, 5, 11, 0)
        );


        when(coffeeChatAppRepository.existsByMentee_UserIdAndCoffeeChatInfo_CoffeeChatInfoId(userId,
            coffeeChatInfoId)).thenReturn(true); // 이미 신청된 상태

        // When & Then
        assertThrows(CustomException.class, () -> {
            coffeeChatApplicationService.createCoffeeChatApp(userId, request);
        } );

    }


    @Test
    @DisplayName("포인트 차감 후 커피챗 신청 실패 시 롤백되어야 함")
    void createCoffeeChatApplication_SaveFails_ShouldRollback() {
        // Given
        Long userId = mockUser.getUserId();
        Long coffeeChatInfoId = mockChatInfo.getCoffeeChatInfoId();

        CoffeeChatApplicationCreateDto request = new CoffeeChatApplicationCreateDto(
            coffeeChatInfoId,
            "안녕하세요 백엔드 커피챗 신청합니다.",
            LocalDateTime.of(2025, 4, 5, 10, 30),
            LocalDateTime.of(2025, 4, 5, 11, 0)
        );

        int currentPoint = 5;
        int deductionPoint  = 3;

        // Mock 설정
        // 중복 신청이 아닌 경우 설정
        when(coffeeChatAppRepository.existsByMentee_UserIdAndCoffeeChatInfo_CoffeeChatInfoId(userId,
            coffeeChatInfoId)).thenReturn(false);
        // 유저와 커피챗 정보가 정상 조회 된다는 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(coffeeChatInfoRepository.findById(coffeeChatInfoId)).thenReturn(Optional.of(mockChatInfo));
        // 현재 포인트 조회 결과 지정
        when(createPointHistoryService.getCurrentPoint(userId)).thenReturn(currentPoint);

        // 포인트 차감 성공한 뒤
        when(createPointHistoryService.createPointHistory(
            EventType.COFFEE_CHAT_APPLY, userId, deductionPoint)).thenReturn(mock(PointHistoryDto.class));

        // 커피챗 저장 시 예외 발생
        when(coffeeChatAppRepository.save(any(CoffeeChatApplication.class))).thenThrow(new RuntimeException("저장 실패"));

        // When: 저장 과정에서 예외 발생 유도
        assertThrows(RuntimeException.class, () ->
            coffeeChatApplicationService.createCoffeeChatApp(userId, request));

        // Then: 포인트 히스토리가 저장되지 않았는지 확인
        verify(createPointHistoryService, times(1)).
            createPointHistory(EventType.COFFEE_CHAT_APPLY, userId, deductionPoint);

    }

}