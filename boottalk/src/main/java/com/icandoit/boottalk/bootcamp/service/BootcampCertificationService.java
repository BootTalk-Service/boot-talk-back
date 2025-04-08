package com.icandoit.boottalk.bootcamp.service;

import java.io.IOException;

import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;
import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.bootcamp.repository.BootcampCertificationRepository;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
import com.icandoit.boottalk.common.service.S3Service;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootcampCertificationService {

	private final S3Service s3Service;
	private final BootcampCertificationRepository bootcampCertificationRepository;
	private final UserRepository userRepository;
	private final CourseRepository courseRepository;

	public BootcampCertification createCertification(MultipartFile file, Long userId, Long courseId) throws
		IOException {
		// 수료증 파일을 S3에 업로드하고 URL 받기
		String fileUrl = s3Service.uploadFile(file, "certifications");

		// BootcampCertification 객체 생성
		BootcampCertification certification = BootcampCertification.builder()
			.fileUrl(fileUrl)
			.status(CertificationStatus.PENDING) // 초기 상태는 PENDING
			.user(userRepository.getReferenceById(userId))
			.course(courseRepository.getReferenceById(courseId))
			.build();

		return bootcampCertificationRepository.save(certification);
	}
}
