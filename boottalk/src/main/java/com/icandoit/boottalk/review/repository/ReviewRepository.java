package com.icandoit.boottalk.review.repository;

import com.icandoit.boottalk.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByUserId(Long userId);
	boolean existsByBootcampId(Long bootcampId);
}
