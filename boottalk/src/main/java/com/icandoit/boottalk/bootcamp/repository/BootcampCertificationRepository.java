package com.icandoit.boottalk.bootcamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.user.domain.entity.User;

public interface BootcampCertificationRepository extends JpaRepository<BootcampCertification, Long> {
	boolean existsByUserAndCourseAndStatusIn(User user, Course course, List<CertificationStatus> blockedStatuses);

	boolean existsByUserAndCourseAndStatusNot(User user, Course course, CertificationStatus status);

	List<BootcampCertification> findAllByUserAndStatus(User user, CertificationStatus certificationStatus);

	List<BootcampCertification> findAllByStatus(CertificationStatus certificationStatus);

	// 유저가 해당 코스에 대해 APPROVED 인증을 가지고 있는지 여부
	boolean existsByUserAndCourseAndStatus(User user, Course course, CertificationStatus certificationStatus);
}
