package com.icandoit.boottalk.bootcamp.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.icandoit.boottalk.bootcamp.dto.CertificationCreationRequestDto;
import com.icandoit.boottalk.bootcamp.dto.CertificationResponseDto;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.bootcamp.repository.BootcampCertificationRepository;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

class BootcampCertificationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private CourseRepository courseRepository;

	@Mock
	private BootcampCertificationRepository bootcampCertificationRepository;

	@InjectMocks
	private BootcampCertificationService certificationService;

	private User testUser;
	private Course testCourse;
	private CertificationCreationRequestDto testRequest;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		// given: 테스트용 User 생성 (필요한 필드만 세팅)
		testUser = User.builder()
			.userId(1L)
			.build();

		// given: 테스트용 Course 생성
		// BootcampCategoryType 예시: SECURITY_ENGINEERING (실제 값으로 대체)
		testCourse =
			Course.builder()
				.courseId(1L)
				.trainingProgramId("TP001")
				.courseName("클라우드 보안엔지니어(화이트해커) 양성")
				.bootcampCategoryType(BootcampCategoryType.APPLICATION_SW_ENGINEERING)
				.trainingCenter(null)
				.build();

		// given: 테스트용 CertificationCreationRequestDto 생성
		// 주의: DTO 순서가 (courseName, fileUrl)임에 주의하여 생성
		testRequest = new CertificationCreationRequestDto(1L, "string");
	}

	@Test
	@DisplayName("인증 등록 성공 - 신규 등록 시")
	public void testCreateCertification_Success() {
		// given
		given(courseRepository.findById(testRequest.courseId()))
			.willReturn(Optional.of(testCourse));
		given(userRepository.getReferenceById(testUser.getUserId()))
			.willReturn(testUser);
		List<CertificationStatus> blockedStatuses = List.of(CertificationStatus.PENDING, CertificationStatus.APPROVED);
		given(bootcampCertificationRepository.existsByUserAndCourseAndStatusIn(testUser, testCourse, blockedStatuses))
			.willReturn(false);
		given(bootcampCertificationRepository.save(argThat(certification ->
			certification != null &&
				certification.getFileUrl().equals("string") &&
				certification.getUser().equals(testUser) &&
				certification.getCourse().equals(testCourse)
		))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		CertificationResponseDto response = certificationService.createCertification(testUser.getUserId(), testRequest);

		// then
		assertNotNull(response);
		assertEquals(testRequest.fileUrl(), response.fileUrl());
		assertEquals(testRequest.courseId(), response.courseId());
		assertEquals(CertificationStatus.PENDING.name(), response.status());
	}

	@Test
	@DisplayName("인증 등록 실패 - 중복 등록 시")
	public void testCreateCertification_Duplicate() {
		// given
		given(courseRepository.findById(testRequest.courseId()))
			.willReturn(Optional.of(testCourse));
		given(userRepository.getReferenceById(testUser.getUserId()))
			.willReturn(testUser);

		List<CertificationStatus> blockedStatuses = List.of(CertificationStatus.PENDING, CertificationStatus.APPROVED);
		given(bootcampCertificationRepository.existsByUserAndCourseAndStatusIn(testUser, testCourse, blockedStatuses))
			.willReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			certificationService.createCertification(testUser.getUserId(), testRequest);
		});

		assertEquals(DUPLICATE_CERTIFICATION_EXIST, exception.getErrorCode());

		verify(courseRepository, times(1)).findById(testRequest.courseId());
		verify(userRepository, times(1)).getReferenceById(testUser.getUserId());
		verify(bootcampCertificationRepository, times(1))
			.existsByUserAndCourseAndStatusIn(testUser, testCourse, blockedStatuses);
	}

	@Test
	@DisplayName("인증 등록 실패 - 존재하지 않는 코스명")
	public void testCreateCertification_CourseNotFound() {
		// given
		given(courseRepository.findById(testRequest.courseId()))
			.willReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			certificationService.createCertification(testUser.getUserId(), testRequest);
		});
		assertEquals(ErrorCode.COURSE_NOT_FOUND.getHttpStatus(),
			exception.getErrorCode().getHttpStatus());
	}
}