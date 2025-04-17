package com.icandoit.boottalk.bootcamp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icandoit.boottalk.bootcamp.dto.TrainingCenterResponseDto;
import com.icandoit.boottalk.bootcamp.service.TrainingCenterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trainingCenter")
@RequiredArgsConstructor
public class TrainingCenterController {

	private final TrainingCenterService trainingCenterService;

	@GetMapping("/{trainingCenterId}")
	public ResponseEntity<TrainingCenterResponseDto> getTrainingCenter(
		@PathVariable("trainingCenterId") Long trainingCenterId)
	{
		return ResponseEntity.ok(trainingCenterService.findById(trainingCenterId));
	}
}
