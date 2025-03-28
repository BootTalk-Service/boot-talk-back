package com.icandoit.boottalk.bootcamp.entity;

import com.icandoit.boottalk.libs.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "training_center")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingCenter extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_center_id")
	private Long id;

	@NotNull(message = "훈련기관 이름은 필수 항목입니다.")
	private String name;

	@NotNull(message = "훈련기관 연락처는 필수 항목입니다.")
	private String phoneNumber;

	@NotNull(message = "훈련기관 이메일은 필수 항목입니다.")
	private String email;

	@NotNull(message = "훈련기관 주소는 필수 항목입니다.")
	private String address;

	@NotNull(message = "훈련기관 이메일은 필수 항목입니다.")
	private String url;
}
