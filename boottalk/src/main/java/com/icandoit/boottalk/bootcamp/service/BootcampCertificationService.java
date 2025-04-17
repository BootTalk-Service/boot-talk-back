package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.notification.type.NotificationType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.bootcamp.dto.CertificationCreationRequestDto;
import com.icandoit.boottalk.bootcamp.dto.CertificationResponseDto;
import com.icandoit.boottalk.bootcamp.dto.CertificationUpdateRequestDto;
import com.icandoit.boottalk.bootcamp.dto.GetCertificationInfoDto;
import com.icandoit.boottalk.bootcamp.dto.GetCertificationResponseDto;
import com.icandoit.boottalk.bootcamp.dto.GetPendingCertificationResponseDto;
import com.icandoit.boottalk.bootcamp.entity.BootcampCertification;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.bootcamp.repository.BootcampCertificationRepository;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.notification.dto.NotificationRequestDto;
import com.icandoit.boottalk.notification.service.SseEmitterService;
import com.icandoit.boottalk.notification.type.NotificationType;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootcampCertificationService {

	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final BootcampCertificationRepository bootcampCertificationRepository;
	private final SseEmitterService sseEmitterService;

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

	public List<GetCertificationResponseDto> getMyCertifications(Long userId) {
		User user = userRepository.getReferenceById(userId);

		List<BootcampCertification> certifications = bootcampCertificationRepository.findAllByUserAndStatus(user, CertificationStatus.APPROVED);

		return certifications.stream()
			.map(GetCertificationResponseDto::from)
			.toList();
	}

	// 승인 대기중인 요청 모두 조회
	public List<GetPendingCertificationResponseDto> getPendingCertifications() {
		List<BootcampCertification> certifications = bootcampCertificationRepository.findAllByStatus(CertificationStatus.PENDING);
		return certifications.stream()
			.map(GetPendingCertificationResponseDto::from)
			.toList();
	}

	// 수료증 정보 조회
	public GetCertificationInfoDto findById(Long certificationId) {
		BootcampCertification certification = bootcampCertificationRepository.getReferenceById(certificationId);

		return GetCertificationInfoDto.from(certification);
	}

	// 수료증 승인 거절 로직
	public CertificationResponseDto updateCertification(CertificationUpdateRequestDto request) {
		BootcampCertification certification = bootcampCertificationRepository.getReferenceById(request.certificationId());

		CertificationStatus newStatus = request.isTrue() ? CertificationStatus.APPROVED : CertificationStatus.REJECTED;
		certification.updateStatus(newStatus);

		BootcampCertification savedCertification = bootcampCertificationRepository.save(certification);

		// 수료증 인증 알림 전송 부분
		Long userId = savedCertification.getUser().getUserId();
		NotificationType type = request.isTrue() ? CERTIFICATE_VERIFIED : CERTIFICATE_REJECTED;

		sendCertificationNotification(userId, type);


		return CertificationResponseDto.from(savedCertification);
	}

	// 수료증 인증 승인 거절 알림 발송
	public void sendCertificationNotification(Long userId, NotificationType type) {
		sseEmitterService.sendToClient(userId, NotificationRequestDto.ofType(type));
	}
}
