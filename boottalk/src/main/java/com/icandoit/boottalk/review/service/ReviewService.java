package com.icandoit.boottalk.review.service;

import com.icandoit.boottalk.bootcamp.entity.Bootcamp;
import com.icandoit.boottalk.bootcamp.repository.CourseRepository;
import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.review.dto.ReviewRequestDto;
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.entity.Review;
import com.icandoit.boottalk.review.repository.ReviewRepository;
import com.icandoit.boottalk.bootcamp.repository.BootcampRepository;
import com.icandoit.boottalk.user.domain.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final BootcampRepository bootcampRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final CourseRepository courseRepository;

	@Transactional
	public ReviewResponseDto create(ReviewRequestDto request, Long userId) {
		// TODO : 리뷰 작성시 해당 코스에 평점 반영하는 로직 필요
		String trainingProgramId = request.trainingProgramId();

		validateCreateReview(trainingProgramId, userId);
		validateCourse(trainingProgramId);

		Review review = Review.of(
			request,
			courseRepository.findByTrainingProgramId(trainingProgramId).get(),
			userRepository.findById(userId).get()
		);
		reviewRepository.save(review);

		// TODO: 포인트 적립 추가
		return ReviewResponseDto.from(review);
	}

	@Transactional(readOnly = true)
	public List<ReviewResponseDto> listAll() {

		List<Review> reviews = reviewRepository.findAll();

		return reviews.stream()
			.map(ReviewResponseDto::from)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ReviewResponseDto> listMy(Long userId) {

		List<Review> reviews = reviewRepository.findByUser_UserId(userId);

		return reviews.stream()
			.map(ReviewResponseDto::from)
			.collect(Collectors.toList());
	}
	
	@Transactional
	public ReviewResponseDto update(ReviewRequestDto request, Long reviewId, Long userId) {
		// TODO : 리뷰 수정시 해당 코스의 평점 업데이트 하는 로직 추가
		Review review = getReview(reviewId);

		validateCourse(review.getCourse().getTrainingProgramId());
		validateReview(review.getUser().getUserId(), userId);

		review.update(request);

		return ReviewResponseDto.from(review);
	}

	@Transactional
	public void delete(Long reviewId, Long userId) {
		// TODO : 리뷰 삭제시 해당 코스의 평점 업데이트하는 로직 추가
		Review review = getReview(reviewId);
		validateCourse(review.getCourse().getTrainingProgramId());
		validateReview(review.getUser().getUserId(), userId);

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
			throw new CustomException(ErrorCode.BOOTCAMP_NOT_FOUND);
		}
	}

	private Bootcamp getBootcamp(Long id) {
		return bootcampRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.BOOTCAMP_NOT_FOUND));
	}

}
