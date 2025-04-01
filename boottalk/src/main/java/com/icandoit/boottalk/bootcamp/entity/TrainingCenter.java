package com.icandoit.boottalk.bootcamp.entity;

import com.icandoit.boottalk.libs.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "training_center")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TrainingCenter extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long trainingCenterId;

	@Column(nullable = false)
	private String trainingCenterName;

	private String trainingCenterPhoneNumber;

	private String trainingCenterEmail;

	private String trainingCenterAddress;

	private String trainingCenterUrl;

	public static TrainingCenter of(String name, String email, String address, String url, String phoneNumber) {
		return TrainingCenter.builder()
			.trainingCenterName(name)
			.trainingCenterEmail(email)
			.trainingCenterAddress(address)
			.trainingCenterUrl(url)
			.trainingCenterPhoneNumber(phoneNumber)
			.build();
	}
}
