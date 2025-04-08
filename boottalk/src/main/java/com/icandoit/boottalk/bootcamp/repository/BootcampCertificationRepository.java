package com.icandoit.boottalk.bootcamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.user.domain.entity.User;

public interface BootcampCertificationRepository extends JpaRepository<BootcampCertification, Long> {
	boolean existsByUserAndCourseAndStatusIn(User user, Course course, List<CertificationStatus> blockedStatuses);
}
