package com.icandoit.boottalk.bootcamp.service;

import org.springframework.stereotype.Service;

import com.icandoit.boottalk.bootcamp.dto.TrainingCenterResponseDto;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.repository.TrainingCenterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingCenterService {

	private final TrainingCenterRepository trainingCenterRepository;

	public TrainingCenterResponseDto findById(Long id) {
		TrainingCenter trainingCenter = trainingCenterRepository.getReferenceById(id);

		return TrainingCenterResponseDto.from(trainingCenter);
	}
}
