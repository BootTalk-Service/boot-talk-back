package com.icandoit.boottalk.review.service;

import com.icandoit.boottalk.libs.exception.CustomException;
import com.icandoit.boottalk.libs.exception.ErrorCode;
import com.icandoit.boottalk.review.dto.ReviewRequestDto;
import com.icandoit.boottalk.review.dto.ReviewResponseDto;
import com.icandoit.boottalk.review.entity.Review;
import com.icandoit.boottalk.review.repository.ReviewRepository;
import com.icandoit.boottalk.test.repository.BootCampRepository;
import com.icandoit.boottalk.test.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final BootCampRepository bootcampRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;


	@Transactional
	public ReviewResponseDto create(ReviewRequestDto request, Long userId) {
		Long bootcampId = request.getBootcampId();

		validateCreateReview(bootcampId, userId);
		validateBootCamp(bootcampId);

		Review review = Review.of(
			request,
			bootcampRepository.findById(bootcampId).get(),
			userRepository.findById(userId).get()
		);
		reviewRepository.save(review);

		// TODO: 포인트 적립 추가

		return ReviewResponseDto.from(review);
	}
	
	public List<ReviewResponseDto> listAll() {

		List<Review> reviews = reviewRepository.findAll();

		return reviews.stream()
			.map(review -> ReviewResponseDto.from(review))
			.collect(Collectors.toList());
	}
	
	public List<ReviewResponseDto> listMy(Long userId) {

		List<Review> reviews = reviewRepository.findByUserId(userId);

		return reviews.stream()
			.map(review -> ReviewResponseDto.from(review))
			.collect(Collectors.toList());
	}
	
	@Transactional
	public ReviewResponseDto update(ReviewRequestDto request, Long reviewId, Long userId) {

		Review review = getReview(reviewId);
		validateBootCamp(review.getBootcamp().getId());
		validateReview(review.getUser().getId(), userId);

		review.update(request);

		return ReviewResponseDto.from(review);
	}

	@Transactional
	public void delete(Long reviewId, Long userId) {

		Review review = getReview(reviewId);
		validateBootCamp(review.getBootcamp().getId());
		validateReview(review.getUser().getId(), userId);

		// TODO: 리뷰를 삭제하면 이미 리뷰 작성으로 적립받은 포인트는 어떻게 되는 것인지?

		reviewRepository.delete(review);

	}

	private Review getReview(Long id) {
		return reviewRepository.findById(id).
			orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
	}

	private void validateCreateReview(Long bootcampId, Long userId) {
		if (reviewRepository.existsByBootcampIdAndUserId(bootcampId, userId)) {
			throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
		}
	}

	private void validateReview(Long reviewUserId, Long userId) {
		if (reviewUserId != userId) {
			throw new CustomException(ErrorCode.NOT_REVIEW_OWNER);
		}
	}

	private void validateBootCamp(Long id) {
		if (!bootcampRepository.existsById(id)) {
			throw new CustomException(ErrorCode.BOOTCAMP_NOT_FOUND);
		}
	}

}
