package com.icandoit.boottalk.coffeeChat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.UserType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatQueryRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CoffeeChatInfoServiceTest {

    @Mock
    private CoffeeChatQueryRepository coffeeChatQueryRepository;

    @InjectMocks
    private CoffeeChatInfoService coffeeChatInfoService;

    private List<CoffeeChatListDto> coffeeChatList;
    private Page<CoffeeChatListDto> coffeeChatPage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        coffeeChatList = Arrays.asList(
            new CoffeeChatListDto(1L, 1L, "test1", UserType.PROFESSIONAL, JobType.BACKEND, "백엔드 현업자 test1입니다."),
            new CoffeeChatListDto(2L, 2L, "test2", UserType.GRADUATE, JobType.FRONTEND,
                "프론트엔드 코스 수료자 test2 입니다.")
        );
        pageable = PageRequest.of(0, 10);
        coffeeChatPage = new PageImpl<>(coffeeChatList, pageable, coffeeChatList.size());
    }

    @Test
    @DisplayName("모든 파라미터가 null일 때 전체 결과를 반환해야 함")
    void getFilteredCoffeeChatResults_WithNullParameters_ReturnsAllResults() {
        // given
        when(coffeeChatQueryRepository.getFilteredCoffeeChatResults(null, null,
            pageable)).thenReturn(coffeeChatPage);

        // when
        Page<CoffeeChatListDto> result = coffeeChatInfoService.getFilteredCoffeeChatResults(null,
            null, pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(coffeeChatQueryRepository, times(1)).getFilteredCoffeeChatResults(null, null,
            pageable);
    }

    @Test
    @DisplayName("JobType으로 필터링 시 해당하는 결과만 반환해야 함")
    void getFilteredCoffeeChatResults_WithJobType_ReturnsFilteredResults() {
        JobType jobType = JobType.BACKEND;

        List<CoffeeChatListDto> filteredList = coffeeChatList.stream()
            .filter(dto -> JobType.BACKEND.equals(dto.jobType()))
            .collect(Collectors.toList());

        Page<CoffeeChatListDto> filteredPage = new PageImpl<>(filteredList, pageable,
            filteredList.size());

        when(coffeeChatQueryRepository.getFilteredCoffeeChatResults(jobType, null, pageable))
            .thenReturn(filteredPage);

        Page<CoffeeChatListDto> result = coffeeChatInfoService.getFilteredCoffeeChatResults(jobType,
            null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(coffeeChatQueryRepository, times(1)).getFilteredCoffeeChatResults(jobType, null,
            pageable);
    }

    @Test
    @DisplayName("UserType으로 필터링 시 해당하는 결과만 반환해야 함")
    void getFilteredCoffeeChatResults_WithUserType_ReturnsFilteredResults() {
        UserType userType = UserType.PROFESSIONAL;

        List<CoffeeChatListDto> filteredList = coffeeChatList.stream()
            .filter(dto -> UserType.PROFESSIONAL.equals(dto.userType()))
            .collect(Collectors.toList());

        Page<CoffeeChatListDto> filteredPage = new PageImpl<>(filteredList, pageable,
            filteredList.size());

        when(coffeeChatQueryRepository.getFilteredCoffeeChatResults(null, userType, pageable))
            .thenReturn(filteredPage);

        // when
        Page<CoffeeChatListDto> result = coffeeChatInfoService.getFilteredCoffeeChatResults(null,
            userType, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(coffeeChatQueryRepository, times(1)).getFilteredCoffeeChatResults(null, userType,
            pageable);
    }

}