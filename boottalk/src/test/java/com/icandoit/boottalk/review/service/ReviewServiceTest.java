package com.icandoit.boottalk.review.service;

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
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
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

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("부트캠프 ID로 해당 코스의 리뷰 리스트를 페이징으로 조회 성공")
	void getReviewsBootcampId_Success() {
	    //given
		Long bootcampId = 1L;
		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		TrainingCenter trainingCenter = TrainingCenter.of("센터", "email", "주소", "url", "010-1234");
		Course course = Course.of("TPID-123", "코스 이름", trainingCenter);

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
	@DisplayName("리뷰 생성 시 Course 평점 및 리뷰 수 증가 확인")
	void createReviewUpdatesCourseStats() {
		//given
		String trainingProgramId = "TPID-123";
		Long userId = 1L;

		Course course = Course.of(trainingProgramId, "TestCourse", null);
		User user = User.builder().userId(userId).userName("testUser").build();

		ReviewCreateRequestDto request = new ReviewCreateRequestDto(trainingProgramId, "this is review", 3);

		when(courseRepository.findWithLockByTrainingProgramId(trainingProgramId)).thenReturn(Optional.of(course));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		//when
		reviewService.createReview(request, userId);

		//then
		assertEquals(3, course.getTotalScore());
		assertEquals(1, course.getReviewCount());
	}

	@Test
	@DisplayName("리뷰 수정 시 Course 평점 반영 확인")
	void updateReviewUpdatesCourseStats() {
		String trainingProgramId = "TP123";
		Long userId = 1L;

		Course course = Course.of(trainingProgramId, "TestCourse", null);
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
	@DisplayName("리뷰 삭제 시 Course 평점 및 리뷰 수 감소 확인")
	void deleteReviewUpdatesCourseStats() {
		String trainingProgramId = "TP123";
		Long userId = 1L;

		Course course = Course.of(trainingProgramId, "TestCourse", null);
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
}