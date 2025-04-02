package com.icandoit.boottalk.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.icandoit.boottalk.bootcamp.entity.Course;
import com.icandoit.boottalk.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByUser_UserId(Long userId);

	boolean existsByCourse_TrainingProgramIdAndUser_UserId(String trainingProgramId, Long userId);

	// 해당 Course 에 속한 리뷰들을 페이징 처리하여 반환
	Page<Review> findByCourse(Course course, Pageable pageable);
}
