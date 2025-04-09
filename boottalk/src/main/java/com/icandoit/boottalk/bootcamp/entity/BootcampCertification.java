package com.icandoit.boottalk.bootcamp.entity;

import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.libs.entity.BaseEntity;
import com.icandoit.boottalk.user.domain.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bootcamp_certification")
public class BootcampCertification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	private String fileUrl;

	@Enumerated(EnumType.STRING)
	private CertificationStatus status;

	public static BootcampCertification of(User user, Course course, String fileUrl) {
		return BootcampCertification.builder()
			.user(user)
			.course(course)
			.fileUrl(fileUrl)
			.status(CertificationStatus.PENDING)
			.build();
	}
}
