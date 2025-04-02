package com.icandoit.boottalk.review.repository;

import com.icandoit.boottalk.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByUser_UserId(Long userId);

	boolean existsByCourse_TrainingProgramIdAndUser_UserId(String trainingProgramId, Long userId);
}
