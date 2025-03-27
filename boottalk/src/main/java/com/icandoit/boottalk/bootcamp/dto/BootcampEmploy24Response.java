package com.icandoit.boottalk.bootcamp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class BootcampEmploy24Response {

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BootcampListResponseDto {

		@JsonProperty("subTitle")
		private String trainingCenterName;

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

		@JsonProperty("trainstCstId")
		private String trainingCenterId;

		@JsonProperty("trprDegr")
		private String bootcampDegree;

		@JsonProperty("trprId")
		private String bootcampId;

		@JsonProperty("yardMan")
		private String maxCapacity;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BootcampDetailResponseDto {

		@JsonProperty("trprTargetNm")
		private String BootcampCourseName;  // 훈련 과정명 : K-디지털트레이닝 등

		@JsonProperty("trprChapTel")
		private String trainingCenterTelephoneNumber;  // TrainingCenter Tel

		@JsonProperty("trprChapEmail")
		private String trainingCenterEmail; // 훈련기관 email

		@JsonProperty("ncsNm")
		private String ncsName;     // ncs 직군 명

		@JsonProperty("addr1")
		private String address1;    // trainingCenter 주소

		@JsonProperty("addr2")
		private String address2;    // trainingCenter 상세주소

		@JsonProperty("hpAddr")
		private String trainingCenterUrl;   // trainingCenter URL

		private String governmentBusinessName;  // 정부 사업명 (inst_detail_info에 있음)

		public void setGovernmentBusinessName(String governmentBusinessName) {
			this.governmentBusinessName = governmentBusinessName;
		}
	}
}

