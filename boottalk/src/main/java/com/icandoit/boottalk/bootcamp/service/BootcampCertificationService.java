package com.icandoit.boottalk.bootcamp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.bootcamp.dto.CertificationCreationRequestDto;
import com.icandoit.boottalk.bootcamp.dto.CertificationResponseDto;
import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.bootcamp.repository.BootcampCertificationRepository;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootcampCertificationService {

	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final BootcampCertificationRepository bootcampCertificationRepository;

	// 수료증 등록
	@Transactional
	public CertificationResponseDto createCertification(Long userId, CertificationCreationRequestDto request) {
		Course course = courseRepository.findById(request.courseId())
			.orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

		User user = userRepository.getReferenceById(userId);

		// 해당 부트캠프에 승인됐거나 요청한 적이 있으면 에러 발생, 거절되었을때는 다시 신청 가능
		if (bootcampCertificationRepository.existsByUserAndCourseAndStatusNot(user, course, CertificationStatus.REJECTED)) {
			throw new CustomException(ErrorCode.DUPLICATE_CERTIFICATION_EXIST);
		}

		BootcampCertification certification =
			BootcampCertification.of(user, course, request.fileUrl());

		BootcampCertification savedCertification = bootcampCertificationRepository.save(certification);

		return CertificationResponseDto.from(savedCertification);
	}
}
