package com.icandoit.boottalk.bootcamp.entity;

import java.time.LocalDate;

import com.icandoit.boottalk.bootcamp.dto.BootcampResponse;
import com.icandoit.boottalk.libs.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bootcamp")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bootcamp extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bootcamp_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_center_id")
	@NotNull(message = "트레이닝 센터는 필수 항목입니다.")
	private TrainingCenter trainingCenter;

	@NotNull(message = "부트캠프 이름은 필수 항목입니다.")
	private String bootcampName;

	@Enumerated(EnumType.STRING)
	@NotNull(message = "부트캠프 카테고리는 필수 항목입니다.")
	private BootcampCategoryType bootcampCategoryType;

	@NotNull(message = "부트캠프 기수는 필수 항목입니다.")
	private int bootcampDegree;

	@NotNull(message = "부트캠프 지역은 필수 항목입니다.")
	private String bootcampRegion;

	@NotNull(message = "부트캠프 정원은 필수 항목입니다.")
	private int bootcampCapacity;

	@NotNull(message = "부트캠프 비용 정보는 필수 항목입니다.")
	private boolean bootcampCost;

	@NotNull(message = "부트캠프 시작일은 필수 항목입니다.")
	private LocalDate bootcampStartDate;

	@NotNull(message = "부트캠프 종료일은 필수 항목입니다.")
	private LocalDate bootcampEndDate;

	@NotNull(message = "부트캠프 링크는 필수 항목입니다.")
	private String bootcampLink;

	// DTO 로부터 Bootcamp 엔티티를 생성하는 메서드
	public static Bootcamp of(
		BootcampResponse.BootcampResponseDto dto, TrainingCenter trainingCenter, BootcampCategoryType categoryType
	) {
		return Bootcamp.builder()
			.trainingCenter(trainingCenter)
			.bootcampName(dto.getBootcampName())
			.bootcampRegion(dto.getBootcampRegion())
			.bootcampCost(dto.getBootcampCost())
			.bootcampLink(dto.getBootcampLink())
			.bootcampCategoryType(categoryType)
			.bootcampDegree(dto.getBootcampDegree())
			.bootcampCapacity(dto.getBootcampCapacity())
			.bootcampStartDate(dto.getBootcampStartDate())
			.bootcampEndDate(dto.getBootcampEndDate())
			.build();
	}
}
