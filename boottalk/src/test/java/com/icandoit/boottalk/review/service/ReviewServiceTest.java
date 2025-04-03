package com.icandoit.boottalk.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.entity.Review;
import com.icandoit.boottalk.review.repository.ReviewRepository;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
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

}