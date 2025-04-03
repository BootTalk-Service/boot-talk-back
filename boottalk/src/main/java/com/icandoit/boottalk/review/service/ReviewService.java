package com.icandoit.boottalk.review.service;

import static com.icandoit.boottalk.bootcamp.exception.BootcampErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.entity.BootcampCategoryType;
import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.bootcamp.exception.BootcampCustomException;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final BootcampRepository bootcampRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final CourseRepository courseRepository;

	@Transactional
	public ReviewResponseDto create(ReviewCreateRequestDto request, Long userId) {
		String trainingProgramId = request.trainingProgramId();

		validateCreateReview(trainingProgramId, userId);

		// 비관적 락으로 course 를 조회
		Course course = getCourseWithLock(trainingProgramId);

		User user = userRepository.getReferenceById(userId);

		Review review = Review.of(request, course, user);

		// course 점수 업데이트
		updateCourseReviewStats(course, request.rating(), 1);

		reviewRepository.save(review);

		// TODO: 포인트 적립 추가
		return ReviewResponseDto.from(review);
	}

	@Transactional(readOnly = true)
	public Page<ReviewResponseDto> listAll(Pageable pageable, String category) {
		if (category != null && !category.isBlank()) {
			if (!BootcampCategoryType.isValidKoreanName(category)) {
				throw new BootcampCustomException(INVALID_CATEGORY_NAME);
			}
			BootcampCategoryType categoryType = BootcampCategoryType.fromKoreanName(category);
			return reviewRepository.findByBootcampCategory(categoryType, pageable)
				.map(ReviewResponseDto::from);
		}

		return reviewRepository.findAll(pageable)
			.map(ReviewResponseDto::from);
	}

	@Transactional(readOnly = true)
	public Page<ReviewResponseDto> listMy(Long userId, Pageable pageable) {
		return reviewRepository.findByUser_UserId(userId, pageable)
			.map(ReviewResponseDto::from);
	}
	
	@Transactional
	public ReviewResponseDto update(ReviewUpdateRequestDto request, Long reviewId, Long userId) {

		Review review = getReview(reviewId);

		validateReview(review.getUser().getUserId(), userId);

		// 비관적 락으로 코드 조회
		Course course = getCourseWithLock(review.getCourse().getTrainingProgramId());

		// 기존 평점 제거 후 새 평점 반영
		updateCourseReviewStats(course, request.rating() - review.getRating(), 0);

		review.update(request);

		return ReviewResponseDto.from(review);
	}

	@Transactional
	public void delete(Long reviewId, Long userId) {
		Review review = getReview(reviewId);

		validateReview(review.getUser().getUserId(), userId);

		Course course = getCourseWithLock(review.getCourse().getTrainingProgramId());

		updateCourseReviewStats(course, -review.getRating(), -1);

		// TODO: 리뷰를 삭제하면 이미 리뷰 작성으로 적립받은 포인트는 어떻게 되는 것인지?
		reviewRepository.delete(review);

	}

	// 부트캠프 ID 로부터 Course 를 조회한 후, 해당 Course 에 작성된 리뷰를 페이징 처리하여 반환
	@Transactional(readOnly = true)
	public Page<ReviewResponseDto> getReviewsBootcampId(Long bootcampId, Pageable pageable) {
		Bootcamp bootcamp = getBootcamp(bootcampId);

		return reviewRepository.findByCourse(bootcamp.getCourse(), pageable)
			.map(ReviewResponseDto::from);
	}

	private Review getReview(Long id) {
		return reviewRepository.findById(id).
			orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
	}

	private void validateCreateReview(String trainingProgramId, Long userId) {
		if (reviewRepository.existsByCourse_TrainingProgramIdAndUser_UserId(trainingProgramId, userId)) {
			throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
		}
	}

	private void validateReview(Long reviewUserId, Long userId) {
		if (reviewUserId != userId) {
			throw new CustomException(ErrorCode.NOT_REVIEW_OWNER);
		}
	}

	private void validateCourse(String trainingProgramId) {
		if (courseRepository.findByTrainingProgramId(trainingProgramId).isEmpty()) {
			throw new CustomException(ErrorCode.COURSE_NOT_FOUND);
		}
	}

	private Bootcamp getBootcamp(Long id) {
		return bootcampRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.BOOTCAMP_NOT_FOUND));
	}

	// 비관적 락을 사용해 course 조회
	private Course getCourseWithLock(String trainingProgramId) {
		return courseRepository.findWithLockByTrainingProgramId(trainingProgramId)
			.orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
	}

	// 리뷰 평점값을 course 에 업데이트
	private void updateCourseReviewStats(Course course, int deltaScore, int deltaCount) {
		int newTotalScore = course.getTotalScore() + deltaScore;
		int newReviewCount = course.getReviewCount() + deltaCount;
		course.updateReviewStats(newTotalScore, newReviewCount);
	}

}
