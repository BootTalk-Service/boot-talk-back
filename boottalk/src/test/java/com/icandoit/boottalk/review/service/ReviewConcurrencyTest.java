package com.icandoit.boottalk.review.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.entity.TrainingCenter;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
import com.icandoit.boottalk.bootcamp.repository.TrainingCenterRepository;
import com.icandoit.boottalk.review.dto.ReviewCreateRequestDto;
import com.icandoit.boottalk.review.repository.ReviewRepository;
import com.icandoit.boottalk.user.domain.entity.User;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

@SpringBootTest
public class ReviewConcurrencyTest {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private TrainingCenterRepository trainingCenterRepository;

	private final String trainingProgramId = "CONCURRENT-TPID";
	private Long userId = 999L;

	private Long trainingCenterId;
	private Long courseId;

	@BeforeEach
	void setUp() {
		// 기존 유저 먼저 삭제 (테스트용이라면 깔끔하게 시작)
		userRepository.findById(userId).ifPresent(userRepository::delete);

		User user = User.builder()
			.userName("동시성유저")
			.desiredCareer(BootcampCategoryType.AI_SERVICE_IMPLEMENTATION)
			.email("testEmail")
			.resourceUserId("test")
			.build();
		user = userRepository.save(user);
		userId = user.getUserId();

		TrainingCenter center = trainingCenterRepository.save(
			TrainingCenter.of("센터", "email", "주소", "url", "010-1234")
		);
		this.trainingCenterId = center.getTrainingCenterId();

		Course course = courseRepository.save(
			Course.of(trainingProgramId, "동시성 테스트 코스", center)
		);
		this.courseId = course.getCourseId();
	}


	@AfterEach
	void tearDown() {
		// 1. 리뷰 먼저 삭제
		reviewRepository.deleteAll();

		// 2. 코스 삭제
		courseRepository.deleteById(courseId);

		// 3. 센터 삭제
		trainingCenterRepository.deleteById(trainingCenterId);

		// 4. 유저 삭제
		userRepository.deleteById(userId);
	}

	@Test
	void testConcurrentReviewCreate() throws InterruptedException {
		int threadCount = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			final int rating = 5;
			executor.submit(() -> {
				try {
					reviewService.createReview(
						new ReviewCreateRequestDto(trainingProgramId, "좋아요", rating),
						userId
					);
				} catch (Exception e) {
					System.out.println("예외 발생: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Course course = courseRepository.findById(courseId).orElseThrow();
		System.out.println("총 점수: " + course.getTotalScore());
		System.out.println("리뷰 수: " + course.getReviewCount());

		assertEquals(5 * threadCount, course.getTotalScore());
		assertEquals(threadCount, course.getReviewCount());
	}
}
