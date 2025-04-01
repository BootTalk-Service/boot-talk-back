package com.icandoit.boottalk.bootcamp.dto;

import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;

public record TrainingCenterResponseDto(
	Long trainingCenterId,
	String trainingCenterName,
	String trainingCenterPhoneNumber,
	String trainingCenterEmail,
	String trainingCenterAddress,
	String trainingCenterUrl
) {
	public static TrainingCenterResponseDto from(TrainingCenter trainingCenter) {
		return new TrainingCenterResponseDto(
			trainingCenter.getTrainingCenterId(),
			trainingCenter.getTrainingCenterName(),
			trainingCenter.getTrainingCenterPhoneNumber(),
			trainingCenter.getTrainingCenterEmail(),
			trainingCenter.getTrainingCenterAddress(),
			trainingCenter.getTrainingCenterUrl()
		);
	}
}
