package com.icandoit.boottalk.review.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.entity.enums.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.enums.CertificationStatus;
import com.icandoit.boottalk.bootcamp.repository.BootcampCertificationRepository;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.review.dto.ReviewCreateRequestDto;
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.dto.ReviewUpdateRequestDto;
import com.icandoit.boottalk.review.entity.Review;
import com.icandoit.boottalk.review.repository.ReviewRepository;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

class ReviewServiceTest {

	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private BootcampRepository bootcampRepository;

	@Mock
	private CourseRepository courseRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BootcampCertificationRepository certificationRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("리뷰 생성 성공 - Course 평점 및 리뷰 수 증가 확인")
	void createReviewUpdatesCourseStats() {
		//given
		String trainingProgramId = "TPID-123";
		Long userId = 1L;

		Course course = Course.of(trainingProgramId, "TestCourse", BootcampCategoryType.AI_SERVICE_IMPLEMENTATION,null);
		User user = User.builder().userId(userId).userName("testUser").build();

		given(reviewRepository.existsByCourse_TrainingProgramIdAndUser_UserId(trainingProgramId, userId))
			.willReturn(false);
		given(!certificationRepository.existsByUserAndCourseAndStatus(user, course, CertificationStatus.APPROVED))
			.willReturn(true);

		ReviewCreateRequestDto request = new ReviewCreateRequestDto(trainingProgramId, "this is review", 3);

		when(courseRepository.findWithLockByTrainingProgramId(trainingProgramId)).thenReturn(Optional.of(course));
		when(userRepository.getReferenceById(userId)).thenReturn(user);

		//when
		reviewService.createReview(request, userId);

		//then
		assertEquals(3, course.getTotalScore());
		assertEquals(1, course.getReviewCount());
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 중복 작성 시 예외 발생")
	void createReviewFailsOnDuplicate() {
		// given
		String trainingProgramId = "TP123";
		Long userId = 1L;

		given(reviewRepository.existsByCourse_TrainingProgramIdAndUser_UserId(trainingProgramId, userId))
			.willReturn(true);

		ReviewCreateRequestDto request = new ReviewCreateRequestDto(trainingProgramId, "중복 리뷰", 4);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> reviewService.createReview(request, userId));

		assertEquals(DUPLICATE_REVIEW, exception.getErrorCode());
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 부트캠프 수료인증이 안된 유저")
	void createReviewFailsWriteReviewForbidden() {
	    //given
		String trainingProgramId = "TP123";
		Long userId = 1L;

		User user = User.builder().userId(userId).userName("testUser").build();

		Course course = Course.builder()
			.courseId(1L)
			.trainingCenter(TrainingCenter.builder().trainingCenterId(1L).build())
			.trainingProgramId(trainingProgramId)
			.build();


		given(reviewRepository.existsByCourse_TrainingProgramIdAndUser_UserId(trainingProgramId, userId))
			.willReturn(false);
		given(!certificationRepository.existsByUserAndCourseAndStatus(user, course, CertificationStatus.APPROVED))
			.willReturn(false);

		when(courseRepository.findWithLockByTrainingProgramId(trainingProgramId)).thenReturn(Optional.of(course));

		ReviewCreateRequestDto request = new ReviewCreateRequestDto(trainingProgramId, "승인 안된 리뷰", 4);


	    //when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> reviewService.createReview(request, userId));

		assertEquals(WRITE_REVIEW_FORBIDDEN, exception.getErrorCode());
	}

	@Test
	@DisplayName("리뷰 조회 성공 - 부트캠프 ID로 해당 코스의 리뷰 리스트를 페이징으로 조회 성공")
	void getReviewsBootcampId_Success() {
	    //given
		Long bootcampId = 1L;
		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		TrainingCenter trainingCenter = TrainingCenter.of("센터", "email", "주소", "url", "010-1234");
		Course course = Course.of("TPID-123", "코스 이름", BootcampCategoryType.APPLICATION_SW_ENGINEERING,trainingCenter);

		User user = User.builder()
			.userId(1L)
			.userName("홍길동")
			.build();

		Bootcamp bootcamp = Bootcamp.builder()
			.bootcampId(bootcampId)
			.course(course)
			.build();

		Review review = Review.builder()
			.reviewId(1L)
			.course(course)
			.user(user)
			.content("좋아요")
			.rating(5)
			.build();

		Page<Review> reviewPage = new PageImpl<>(List.of(review));

		given(bootcampRepository.findById(bootcampId)).willReturn(Optional.of(bootcamp));
		given(reviewRepository.findByCourse(course, pageable)).willReturn(reviewPage);

	    //when
		Page<ReviewResponseDto> result = reviewService.getReviewsBootcampId(bootcampId, pageable);

	    //then
		ReviewResponseDto dto = result.getContent().get(0);

		assertEquals(1, result.getTotalElements());
		assertEquals(review.getReviewId(), dto.reviewId());
		assertEquals(course.getTrainingProgramId(), dto.trainingProgramId());
		assertEquals(course.getCourseName(), dto.courseName());
		assertEquals(user.getUserName(), dto.userName());
		assertEquals(review.getContent(), dto.content());
		assertEquals(review.getRating(), dto.rating());
	}

	@Test
	@DisplayName("리뷰 조회 실패 - 부트캠프 없음")
	void getReviewsBootcampIdFailsWhenBootcampNotFound() {
		// given
		Long bootcampId = 999L;
		Pageable pageable = PageRequest.of(0, 10);

		when(bootcampRepository.findById(bootcampId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> reviewService.getReviewsBootcampId(bootcampId, pageable));

		assertEquals(BOOTCAMP_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("리뷰 수정 성공 - Course 평점 반영 확인")
	void updateReviewUpdatesCourseStats() {
		String trainingProgramId = "TP123";
		Long userId = 1L;

		Course course = Course.of(trainingProgramId, "TestCourse", BootcampCategoryType.APPLICATION_SW_ENGINEERING, null);
		course.updateReviewStats(4, 1); // 기존 평점 4점 1개

		User user = User.builder().userId(userId).userName("testUser").build();
		Review review = Review.builder()
			.reviewId(1L)
			.user(user)
			.course(course)
			.rating(4)
			.content("this is review")
			.build();

		ReviewUpdateRequestDto request = new ReviewUpdateRequestDto("Updated", 5);

		when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
		when(courseRepository.findWithLockByTrainingProgramId(trainingProgramId)).thenReturn(Optional.of(course));

		reviewService.updateReview(request, 1L, userId);

		assertEquals(5, course.getTotalScore());
		assertEquals(1, course.getReviewCount());
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 유저 불일치")
	void updateReviewFailsWhenNotOwner() {
		// given
		Long reviewId = 1L;
		Long ownerId = 1L;
		Long anotherUserId = 2L;

		Course course = Course.of("TP123", "course", BootcampCategoryType.APPLICATION_SW_ENGINEERING, null);
		User user = User.builder().userId(ownerId).build();
		Review review = Review.builder().reviewId(reviewId).user(user).course(course).rating(3).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> reviewService.updateReview(new ReviewUpdateRequestDto("new content", 5), reviewId, anotherUserId));

		assertEquals(NOT_REVIEW_OWNER, exception.getErrorCode());
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 리뷰 없음")
	void updateReviewFailsWhenReviewNotFound() {
		// given
		Long reviewId = 1L;
		Long userId = 1L;

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> reviewService.updateReview(new ReviewUpdateRequestDto("updated", 5), reviewId, userId));

		assertEquals(REVIEW_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("리뷰 삭제 성공 - Course 평점 및 리뷰 수 감소 확인")
	void deleteReviewUpdatesCourseStats() {
		String trainingProgramId = "TP123";
		Long userId = 1L;

		Course course = Course.of(trainingProgramId, "TestCourse", BootcampCategoryType.APPLICATION_SW_ENGINEERING, null);
		course.updateReviewStats(4, 1); // 평점 4점 1개

		User user = User.builder().userId(userId).userName("testUser").build();
		Review review = Review.builder().reviewId(1L).user(user).course(course).rating(4).build();

		when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
		when(courseRepository.findWithLockByTrainingProgramId(trainingProgramId)).thenReturn(Optional.of(course));

		reviewService.deleteReview(1L, userId);

		assertEquals(0, course.getTotalScore());
		assertEquals(0, course.getReviewCount());
		verify(reviewRepository).delete(review);
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - 리뷰 없음")
	void deleteReviewFailsWhenNotFound() {
		// given
		Long reviewId = 1L;
		Long userId = 1L;

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> reviewService.deleteReview(reviewId, userId));

		assertEquals(REVIEW_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("리뷰 삭제 실패 - 유저가 리뷰 작성자가 아님")
	void deleteReviewFailsWhenNotOwner() {
		// given
		Long reviewId = 1L;
		Long reviewOwnerId = 1L;
		Long anotherUserId = 2L;

		Course course = Course.of("TP123", "CourseName", BootcampCategoryType.APPLICATION_SW_ENGINEERING, null);
		User owner = User.builder().userId(reviewOwnerId).build();
		Review review = Review.builder().reviewId(reviewId).user(owner).course(course).rating(4).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> reviewService.deleteReview(reviewId, anotherUserId));

		assertEquals(NOT_REVIEW_OWNER, exception.getErrorCode());
	}
}