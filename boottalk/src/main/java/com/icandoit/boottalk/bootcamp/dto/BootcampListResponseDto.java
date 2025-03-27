package com.icandoit.boottalk.bootcamp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BootcampListResponseDto {

	@JsonProperty("address")
	private String trainingCenterLocation;

	// 정부 지원금 제외한 실제 금액
	@JsonProperty("courseMan")
	private int totalCost;

	@JsonProperty("instCd")
	private String trainingCenterCode;

	@JsonProperty("ncsCd")
	private String ncsCode;


	@JsonProperty("subTitle")
	private String trainingCenterName;

	@JsonProperty("telNo")
	private String trainingCenterTelNumber;

	@JsonProperty("title")
	private String bootcampName;

	@JsonProperty("titleLink")
	private String bootcampLink;

	@JsonProperty("subTitleLink")
	private String trainingCenterLink;

	@JsonProperty("traEndDate")
	private String trainingEndDate;

	@JsonProperty("traStartDate")
	private String trainingStartDate;

	@JsonProperty("trainTarget")
	private String trainingTarget; // 훈련 대상 : K-디지털 트레이닝

	@JsonProperty("trainTargetCd")
	private String trainingTargetCode; // K-디지털 트레이닝의 코드 번호

	@JsonProperty("trainstCstId")
	private String trainingCenterId;

	@JsonProperty("trprDegr")
	private String bootcampDegree;

	@JsonProperty("trprId")
	private String bootcampId;

	@JsonProperty("yardMan")
	private String maxCapacity;
}
