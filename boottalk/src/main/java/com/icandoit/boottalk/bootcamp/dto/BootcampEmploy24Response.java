package com.icandoit.boottalk.bootcamp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BootcampEmploy24Response {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record BootcampListResponseDto(
		@JsonProperty("subTitle") String trainingCenterName,
		@JsonProperty("title") String bootcampName,
		@JsonProperty("titleLink") String bootcampLink,
		@JsonProperty("subTitleLink") String trainingCenterLink,
		@JsonProperty("traEndDate") String trainingEndDate,
		@JsonProperty("traStartDate") String trainingStartDate,
		@JsonProperty("trainstCstId") String trainingCenterId,
		@JsonProperty("trprDegr") String bootcampDegree,
		@JsonProperty("trprId") String bootcampId,
		@JsonProperty("yardMan") String maxCapacity,
		@JsonProperty("courseMan") String cost
	) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record BootcampDetailResponseDto(
		@JsonProperty("trprTargetNm") String bootcampCourseName,  // 훈련 과정명 : K-디지털트레이닝 등
		@JsonProperty("trprChapTel") String trainingCenterTelephoneNumber,  // TrainingCenter Tel
		@JsonProperty("trprChapEmail") String trainingCenterEmail, // 훈련기관 email
		@JsonProperty("ncsNm") String ncsName,     // ncs 직군 명
		@JsonProperty("addr1") String address1,    // trainingCenter 주소
		@JsonProperty("addr2") String address2,    // trainingCenter 상세주소
		@JsonProperty("hpAddr") String trainingCenterUrl   // trainingCenter URL
	) {
	}
}

