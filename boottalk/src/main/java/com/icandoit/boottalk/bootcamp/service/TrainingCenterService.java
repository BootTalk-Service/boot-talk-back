package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.icandoit.boottalk.bootcamp.dto.TrainingCenterResponseDto;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
import com.icandoit.boottalk.bootcamp.repository.TrainingCenterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingCenterService {

	private final TrainingCenterRepository trainingCenterRepository;

	public TrainingCenterResponseDto findById(Long id) {
		Optional<TrainingCenter> trainingCenter = trainingCenterRepository.findById(id);

		if(trainingCenter.isEmpty()) {
			throw new BootcampCustomException(TRAINING_CENTER_NOT_FOUND);
		}

		return TrainingCenterResponseDto.from(trainingCenter.get());
	}
}
