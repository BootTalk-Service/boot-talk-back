package com.icandoit.boottalk.user.domain.entity;

import java.sql.Timestamp;

import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.libs.entity.BaseEntity;

import com.icandoit.boottalk.user.domain.form.UpdateForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false, updatable = false)
	private String userName;

	@Column(nullable = false)
	private String email;

	private String profileImage;
	//네이버에서 주는 고유 Id
	@Column(nullable = false)
	private String resourceUserId;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(50)", nullable = false)
	private BootcampCategoryType desiredCareer;

	@Setter
	private Timestamp deletedAt;

	private boolean admin;


	public User updateOf(UpdateForm form) {
		this.profileImage = form.profileImage();
		this.desiredCareer = form.desiredCareer();
		return this;
	}
}
