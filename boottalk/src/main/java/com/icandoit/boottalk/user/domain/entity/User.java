package com.icandoit.boottalk.user.domain.entity;

import java.sql.Timestamp;

import com.icandoit.boottalk.libs.entity.BaseEntity;
import com.icandoit.boottalk.user.domain.form.SignUpForm;
import com.icandoit.boottalk.user.domain.form.UpdateForm;
import com.icandoit.boottalk.user.domain.type.DesiredCareer;

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
	private long userId;

	@Column(nullable = false)
	private String userName;

	@Column(nullable = false)
	private String email;

	private String profileImage;
	//네이버에서 주는 고유 Id
	@Column(nullable = false)
	private String resourceUserId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private DesiredCareer desiredCareer;

	@Setter
	private Timestamp deletedAt;

	public static User of(SignUpForm form) {
		return User.builder()
			.userName(form.getUserName())
			.email(form.getEmail())
			.profileImage(form.getProfileImage())
			.resourceUserId(form.getResourceUserId())
			.desiredCareer(form.getDesiredCareer())
			.deletedAt(null)
			.build();
	}

	public User updateOf(UpdateForm form) {
		this.userName = form.getName();
		this.email = form.getEmail();
		this.profileImage = form.getProfileImage();
		this.desiredCareer = form.getDesiredCareer();

		return this;
	}

}
