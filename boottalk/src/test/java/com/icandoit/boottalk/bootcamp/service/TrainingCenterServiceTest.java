package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.icandoit.boottalk.bootcamp.dto.TrainingCenterResponseDto;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.repository.TrainingCenterRepository;
import com.icandoit.boottalk.libs.exception.CustomException;

class TrainingCenterServiceTest {

	@InjectMocks
	private TrainingCenterService trainingCenterService;

	@Mock
	private TrainingCenterRepository trainingCenterRepository;

	private TrainingCenter trainingCenter;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		trainingCenter = TrainingCenter.builder()
			.trainingCenterId(1L)
			.trainingCenterName("기관명")
			.trainingCenterAddress("기관 주소")
			.trainingCenterEmail("example@test.com")
			.trainingCenterPhoneNumber("010-1234-5678")
			.trainingCenterUrl("www.trainingcenter.com")
			.build();
	}

	@Test
	@DisplayName("교육 기관 ID 조회 성공")
	void findByIdSuccess() {
		//given
		when(trainingCenterRepository.findById(1L)).thenReturn(Optional.of(trainingCenter));

		//when
		TrainingCenterResponseDto response = trainingCenterService.findById(1L);

		//then
		assertNotNull(response);
		assertEquals(response.trainingCenterId(), trainingCenter.getTrainingCenterId());
		assertEquals(response.trainingCenterName(), trainingCenter.getTrainingCenterName());
		assertEquals(response.trainingCenterPhoneNumber(), trainingCenter.getTrainingCenterPhoneNumber());
		assertEquals(response.trainingCenterEmail(), trainingCenter.getTrainingCenterEmail());
		assertEquals(response.trainingCenterAddress(), trainingCenter.getTrainingCenterAddress());
		assertEquals(response.trainingCenterUrl(), trainingCenter.getTrainingCenterUrl());
		verify(trainingCenterRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("교육 기관 ID 조회 실패 - 교육 기관 없음")
	void findByIdFailedTrainingCenterNotFound() {
		//given
		when(trainingCenterRepository.findById(9999L)).thenReturn(Optional.empty());

		//when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> trainingCenterService.findById(9999L));

		assertEquals(exception.getErrorCode(), TRAINING_CENTER_NOT_FOUND);
		verify(trainingCenterRepository, times(1)).findById(9999L);
	}

}