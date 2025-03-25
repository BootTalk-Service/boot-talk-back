package com.icandoit.boottalk.review.service;

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

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final BootCampRepository bootcampRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;

	/**
	 * 리뷰 등록
	 * @param request
	 * @param userId
	 * @return
	 */
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

	/**
	 * 리뷰 목록 조회
	 * @return
	 */
	public List<ReviewResponseDto> listAll() {

		List<Review> reviews = reviewRepository.findAll();

		return reviews.stream()
			.map(review -> ReviewResponseDto.from(review))
			.collect(Collectors.toList());
	}

	/**
	 * 내 리뷰 목록 조회
	 * @param userId
	 * @return
	 */
	public List<ReviewResponseDto> listMy(Long userId) {

		List<Review> reviews = reviewRepository.findByUserId(userId);

		return reviews.stream()
			.map(review -> ReviewResponseDto.from(review))
			.collect(Collectors.toList());
	}

	/**
	 * 내 리뷰 수정
	 * @param request
	 * @param reviewId
	 * @param userId
	 * @return
	 */
	public ReviewResponseDto update(ReviewRequestDto request, Long reviewId, Long userId) {

		Review review = getReview(reviewId);
		validateBootCamp(review.getBootcamp().getId());
		validateReview(review.getUser().getId(), userId);

		review.setContent(request.getContent());
		review.setRating(request.getRating());
		reviewRepository.save(review);

		return ReviewResponseDto.from(review);
	}

	/**
	 * 내 리뷰 삭제
	 * @param reviewId
	 * @param userId
	 */
	public void delete(Long reviewId, Long userId) {

		Review review = getReview(reviewId);
		validateBootCamp(review.getBootcamp().getId());
		validateReview(review.getUser().getId(), userId);

		// TODO: 리뷰를 삭제하면 이미 리뷰 작성으로 적립받은 포인트는 어떻게 되는 것인지?

		reviewRepository.delete(review);

	}

	private Review getReview(Long id) {
		return reviewRepository.findById(id).
			orElseThrow(()-> new RuntimeException("존재하지 않는 리뷰입니다."));
	}


	private void validateCreateReview(Long bootcampId, Long userID) {
		if (reviewRepository.existsByBootcampIdAndUserId(bootcampId, userID)) {
			throw new RuntimeException("해당 부트캠프에 이미 리뷰를 작성하였습니다.");
		}
	}

	private void validateReview(Long reviewUserId, Long userId) {
		if (reviewUserId != userId) {
			throw new RuntimeException("사용자가 작성한 리뷰가 아닙니다.");
		}
	}

	private void validateBootCamp(Long id) {
		if (!bootcampRepository.existsById(id)) {
			throw new RuntimeException("존재하지 않는 부트캠프입니다.");
		}
	}


}
