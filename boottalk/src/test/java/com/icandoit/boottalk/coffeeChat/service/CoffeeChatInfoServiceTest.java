package com.icandoit.boottalk.coffeeChat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatListDto;
import com.icandoit.boottalk.coffeeChat.entity.enums.JobType;
import com.icandoit.boottalk.coffeeChat.entity.enums.MentorType;
import com.icandoit.boottalk.coffeeChat.repository.CoffeeChatQueryRepository;
import com.icandoit.boottalk.common.dto.PagedResponseDto;

@ExtendWith(MockitoExtension.class)
class CoffeeChatInfoServiceTest {

    @Mock
    private CoffeeChatQueryRepository coffeeChatQueryRepository;

    @InjectMocks
    private CoffeeChatQueryService coffeeChatQueryService;

    private List<CoffeeChatListDto> coffeeChatList;
    private PagedResponseDto<CoffeeChatListDto> coffeeChatPage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        coffeeChatList = Arrays.asList(
            new CoffeeChatListDto(1L, 1L, "test1", MentorType.PROFESSIONAL, JobType.BACKEND,
                "백엔드 현업자 test1입니다."),
            new CoffeeChatListDto(2L, 2L, "test2", MentorType.GRADUATE, JobType.FRONTEND,
                "프론트엔드 코스 수료자 test2 입니다.")
        );
        pageable = PageRequest.of(0, 10);
        coffeeChatPage = PagedResponseDto.from(new PageImpl<>(coffeeChatList, pageable, coffeeChatList.size()));
    }

    @Test
    @DisplayName("모든 파라미터가 null일 때 전체 결과를 반환해야 함")
    void getFilteredCoffeeChatResults_WithNullParameters_ReturnsAllResults() {
        // given
        when(coffeeChatQueryRepository.getFilteredCoffeeChatResults(null, null,
            pageable)).thenReturn(coffeeChatPage);

        // when
        PagedResponseDto<CoffeeChatListDto> result = coffeeChatQueryService.getFilteredCoffeeChatResults(null,
            null, pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.pagination().totalItems());
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

        PagedResponseDto<CoffeeChatListDto> filteredPage =
            PagedResponseDto.from(new PageImpl<>(filteredList, pageable, filteredList.size()));

        when(coffeeChatQueryRepository.getFilteredCoffeeChatResults(jobType, null, pageable))
            .thenReturn(filteredPage);

        PagedResponseDto<CoffeeChatListDto> result = coffeeChatQueryService.getFilteredCoffeeChatResults(
            jobType,
            null, pageable);

        assertNotNull(result);
        assertEquals(1, result.pagination().totalItems());
        verify(coffeeChatQueryRepository, times(1)).getFilteredCoffeeChatResults(jobType, null,
            pageable);
    }

    @Test
    @DisplayName("UserType으로 필터링 시 해당하는 결과만 반환해야 함")
    void getFilteredCoffeeChatResults_WithUserType_ReturnsFilteredResults() {
        MentorType userType = MentorType.PROFESSIONAL;

        List<CoffeeChatListDto> filteredList = coffeeChatList.stream()
            .filter(dto -> MentorType.PROFESSIONAL.equals(dto.mentorType()))
            .collect(Collectors.toList());

        PagedResponseDto<CoffeeChatListDto> filteredPage =
            PagedResponseDto.from(new PageImpl<>(filteredList, pageable, filteredList.size()));

        when(coffeeChatQueryRepository.getFilteredCoffeeChatResults(null, userType, pageable))
            .thenReturn(filteredPage);

        // when
        PagedResponseDto<CoffeeChatListDto> result = coffeeChatQueryService.getFilteredCoffeeChatResults(null,
            userType, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.pagination().totalItems());
        verify(coffeeChatQueryRepository, times(1)).getFilteredCoffeeChatResults(null, userType,
            pageable);
    }

}